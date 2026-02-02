package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpecificationUtils {

    private SpecificationUtils() {
    }

    public static Collection<Long> employeesOrganizationIds(Collection<Employee> employees) {
        return CareCoordinationUtils.getOrganizationIdsSet(employees);
    }

    public static Collection<Long> employeesCommunityIds(Collection<Employee> employees) {
        return CareCoordinationUtils.getCommunityIdsSet(employees);
    }

    public static Expression<String> employeeFullName(Path<Employee> employeeFrom, CriteriaBuilder cb) {
        return fullName(employeeFrom.get(Employee_.firstName), employeeFrom.get(Employee_.lastName), " ", cb);
    }

    private static Expression<String> fullName(Path<String> first, Path<String> last, String delimiter, CriteriaBuilder cb) {
        return cb.concat(cb.concat(cb.coalesce(first, ""), delimiter), cb.coalesce(last, ""));
    }

    public static String wrapWithWildcards(String name) {
        return "%" + name + "%";
    }

    public static String fixForEnum(String name) {
        return StringUtils.defaultString(name).replaceAll(" ", "_");
    }

    public static <T extends Temporal> Expression<String> americanDateFormat(Path<T> date, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.function("FORMAT", String.class, date, criteriaBuilder.literal("MM/dd/yyyy"));
    }

    public static <T> Subquery<T> subquery(Class<T> tClass, CriteriaQuery criteriaQuery, Function<Root<T>, Predicate> restriction) {
        var sub = criteriaQuery.subquery(tClass);
        var subRoot = sub.from(tClass);
        sub.select(subRoot);
        sub.where(restriction.apply(subRoot));
        return sub;
    }

    public static <T> Predicate in(CriteriaBuilder cb, Expression<T> inPath, Collection<? extends T> values) {
        if (CollectionUtils.isEmpty(values)) {
            return cb.or();
        }
        return cb.<Object>in(inPath).value(values);
    }

    public static <Y extends Comparable<? super Y>> Expression<Y> nullSafeGreatest(CriteriaBuilder cb, Expression<Y> x, Expression<Y> y) {
        return cb.<Y>selectCase()
                .when(cb.and(cb.isNotNull(x), cb.isNull(y)), x)
                .when(cb.and(cb.isNull(x), cb.isNotNull(y)), y)
                .when(cb.greaterThan(x, y), x)
                .otherwise(y);
    }

    public static <Y extends Comparable<? super Y>> Expression<Y> greatest(CriteriaBuilder cb, Expression<Y> x, Expression<Y> y) {
        return cb.<Y>selectCase()
                .when(cb.greaterThan(x, y), x)
                .otherwise(y);
    }

    public static Predicate isNotTrue(CriteriaBuilder cb, Path<Boolean> path) {
        return cb.or(cb.isNull(path), cb.isFalse(path));
    }

    public static <T> Specification<T> and() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and();
    }

    public static <T> Specification<T> or() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or();
    }

    public static <T extends String> Expression<String> deleteMultipleSpaces(Path<T> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.function("REPLACE", String.class,
                criteriaBuilder.function("REPLACE", String.class,
                        criteriaBuilder.function("REPLACE", String.class, path, criteriaBuilder.literal(" "), criteriaBuilder.literal("[]")),
                        criteriaBuilder.literal("]["),
                        criteriaBuilder.literal("")),
                criteriaBuilder.literal("[]"),
                criteriaBuilder.literal(" "));
    }

    public static Predicate byNameLike(Path<String> firstName, Path<String> middleName, Path<String> lastName,
                                       String searchCriteria, CriteriaBuilder criteriaBuilder) {
        if (StringUtils.isEmpty(searchCriteria)) {
            return criteriaBuilder.and();
        }

        var names = Arrays.stream(CareCoordinationUtils.trimAndRemoveMultipleSpaces(searchCriteria).split(" "))
                .map(SpecificationUtils::wrapWithWildcards).collect(Collectors.toList());

        var likeInClientPredicates = names.stream()
                .map(namePart -> {
                    var firstLastNamePredicate = criteriaBuilder.or(
                            criteriaBuilder.like(firstName, namePart),
                            criteriaBuilder.like(lastName, namePart)
                    );
                    if (middleName != null) {
                        return criteriaBuilder.or(
                                firstLastNamePredicate,
                                criteriaBuilder.like(middleName, namePart)
                        );
                    }
                    return firstLastNamePredicate;
                })
                .toArray(Predicate[]::new);
        return criteriaBuilder.and(likeInClientPredicates);
    }

    public interface PathSpecification<T> {
        Predicate toPredicate(Path<T> path, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder);
    }
}
