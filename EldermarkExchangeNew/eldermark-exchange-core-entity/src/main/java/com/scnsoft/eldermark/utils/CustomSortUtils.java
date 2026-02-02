package com.scnsoft.eldermark.utils;

import com.scnsoft.eldermark.beans.pagination.PrependedElementsPageable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CustomSortUtils {

    private static final Logger logger = LoggerFactory.getLogger(CustomSortUtils.class);

    public static final String EXPRESSION_ORDER_PREFIX = "#expression:";

    private CustomSortUtils() {
    }

    public static final class Functions {
        public static final String FUNCTION_PREFIX = "#";
        public static final String FIRST_NON_NULL = FUNCTION_PREFIX + "firstNonNull";

        private Functions() {
        }
    }

    public static Pageable unsortedPage(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }
        if (pageable instanceof PrependedElementsPageable) {
            var prependedPageable = (PrependedElementsPageable) pageable;
            return new PrependedElementsPageable(
                    PageRequest.of(
                            //values should be taken from delegate because pageSize is recalculated in PrependedElementsPageable
                            prependedPageable.getDelegate().getPageNumber(),
                            prependedPageable.getDelegate().getPageSize()
                    ),
                    prependedPageable.getPrependedCount()
            );
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    public static <T> Specification<T> withExpressionSort(Sort sort) {
        return (root, query, criteriaBuilder) -> {
            if (sort.isSorted()) {
                var jpaOrders = sort.get()
                        .map(o -> CustomSortUtils.toJpaOrder(o, root, criteriaBuilder))
                        .collect(Collectors.toList());

                query.orderBy(jpaOrders);
            }
            return criteriaBuilder.and();
        };
    }

    private static javax.persistence.criteria.Order toJpaOrder(Sort.Order order, Root<?> root, CriteriaBuilder criteriaBuilder) {
        if (isExpressionOrder(order)) {
            return toExpressionOrder(order, root, criteriaBuilder);
        } else {
            return QueryUtils.toOrders(Sort.by(order), root, criteriaBuilder).stream().findFirst().orElseThrow();
        }
    }

    private static boolean isExpressionOrder(Sort.Order order) {
        return order.getProperty().startsWith(EXPRESSION_ORDER_PREFIX);
    }

    private static Order toExpressionOrder(Sort.Order order, Root<?> root, CriteriaBuilder criteriaBuilder) {
        var expression = order.getProperty().substring(EXPRESSION_ORDER_PREFIX.length()).trim();
        var ast = buildAst(expression);

        return buildExpression(ast, root, criteriaBuilder, order.getDirection(), order.getNullHandling());
    }

    private static SyntaxTree buildAst(String expression) {
        var tokens = tokenize(expression);
        return buildAst(tokens);
    }

    private static List<String> tokenize(String expression) {
        //brackets are separate tokens - used as function arguments start and end
        expression = expression.replaceAll("\\(", " ( ")
                .replaceAll("\\)", " ) ");

        return Stream.of(expression.split("[ ,]"))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    private static SyntaxTree buildAst(List<String> tokens) {
        var functionsStack = new ArrayDeque<String>();
        var argsCountStack = new ArrayDeque<Integer>();
        var nodesStack = new ArrayDeque<SyntaxTree.Node>();

        for (var token : tokens) {
            if (token.startsWith(Functions.FUNCTION_PREFIX)) {
                functionsStack.push(token);
                if (!argsCountStack.isEmpty()) {
                    //if current function is inside another function - it is still an argument
                    var prev = argsCountStack.pop();
                    argsCountStack.push(prev + 1);
                }
                continue;
            }
            if ("(".equals(token)) {
                argsCountStack.push(0);
                continue;
            }

            if (")".equals(token)) {
                if (argsCountStack.isEmpty() || functionsStack.isEmpty()) {
                    throw new RuntimeException("invalid expression: unexpected ')'");
                }
                var argsCount = argsCountStack.pop();
                var args = new ArrayList<SyntaxTree.Node>();
                for (int i = 0; i < argsCount; ++i) {
                    args.add(nodesStack.pop());
                }
                Collections.reverse(args);
                nodesStack.push(SyntaxTree.Node.function(functionsStack.pop(), args));
                continue;
            }

            nodesStack.push(SyntaxTree.Node.argument(token));
            var prev = argsCountStack.pop();
            argsCountStack.push(prev + 1);
        }

        if (nodesStack.size() != 1) {
            logger.warn("Expected root function. Please double check the expression. Tokens are: " + tokens.toString());
            throw new RuntimeException("Illegal sorting expression");
        }

        return new SyntaxTree(nodesStack.pop());
    }

    private static Order buildExpression(SyntaxTree ast, Root<?> root, CriteriaBuilder criteriaBuilder, Sort.Direction direction, Sort.NullHandling nullHandling) {
        var sort = processNode(ast.getRoot(), root, criteriaBuilder);

        //todo deal with null handling. JPA doesn't have API for that and it is maybe not working for Spring Data Jpa at all.
        if (direction.isAscending()) {
            return criteriaBuilder.asc(sort);
        }
        return criteriaBuilder.desc(sort);
    }

    private static Expression<?> processNode(SyntaxTree.Node node, Root<?> root, CriteriaBuilder criteriaBuilder) {
        if (node.isFunction()) {
            //although string switch is not a best practice, it's currently just simpler
            switch (node.value) {
                case Functions.FIRST_NON_NULL:
                    var args = new ArrayList<Expression<?>>();
                    for (var arg : node.children) {
                        args.add(processNode(arg, root, criteriaBuilder));
                    }
                    return criteriaBuilder.function(args.size() > 2 ? "COALESCE" : "ISNULL", Object.class,
                            args.toArray(new Expression<?>[0]));
                default:
                    logger.warn("Unrecognized function " + node.value);
                    throw new IllegalArgumentException("Error during order construction - unrecognized function");
            }
        }

        if (node.value.startsWith("'") && node.value.endsWith("'")) {
            var stringLiteral = node.value.substring(1, node.value.length() - 1);
            return criteriaBuilder.literal(stringLiteral);
        }

        try {
            var numberLiteral = NumberFormat.getInstance().parse(node.value);
            return criteriaBuilder.literal(numberLiteral);
        } catch (ParseException e) {
            // not a number, go further
        }

        //property
        var order = QueryUtils.toOrders(Sort.by(node.value), root, criteriaBuilder).stream().findFirst().orElseThrow();
        return order.getExpression();
    }

    private static class SyntaxTree {

        private Node root;

        public SyntaxTree(Node root) {
            this.root = root;
        }

        public Node getRoot() {
            return root;
        }

        static class Node {
            private String value;
            private List<Node> children;

            private Node(String value, List<Node> children) {
                this.value = value;
                this.children = children;
            }

            static Node function(String f, List<Node> args) {
                return new Node(f, args);
            }

            static Node argument(String arg) {
                return new Node(arg, null);
            }

            boolean isFunction() {
                //potentially there can be functions with no args, therefore null checking
                //instead of collection empty checking
                return children != null;
            }

            boolean isArgument() {
                return !isFunction();
            }
        }
    }
}
