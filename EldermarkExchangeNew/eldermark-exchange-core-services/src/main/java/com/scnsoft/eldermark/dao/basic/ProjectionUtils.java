package com.scnsoft.eldermark.dao.basic;

import com.scnsoft.eldermark.dao.basic.evaluated.params.EvaluatedPropertyParams;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.criteria.*;
import java.lang.reflect.Method;
import java.util.*;

public final class ProjectionUtils {

    private ProjectionUtils() {
    }

    public static <E> List<Selection<?>> createSelections(AppProjectionReturnedType retType,
                                                          Root<E> root,
                                                          CriteriaQuery<?> query,
                                                          CriteriaBuilder criteriaBuilder,
                                                          Iterable<String> requiredProperties,
                                                          Map<String, EvaluatedPropertyParams> evaluatedPropertiesParams) {
        List<Selection<?>> selections = new ArrayList<>();

        var missingRequiredProperties = new TreeSet<>(IterableUtils.toList(requiredProperties));

        for (var property : retType.getInputProperties()) {
            Expression<?> selectExpression;
            if (property.getEvaluatedPropertyProcessor() == null) {
                selectExpression = JpaUtils.toExpressionRecursively(root, property.getPropertyPath(), true);
            } else {
                var params = evaluatedPropertiesParams.getOrDefault(property.getAlias(), null);
                selectExpression = property.getEvaluatedPropertyProcessor()
                        .createExpression(root, query, criteriaBuilder, property.getAlias(), params);
            }
            selections.add(selectExpression.alias(property.getAlias()));
            missingRequiredProperties.remove(property.getAlias());
        }

        for (var propName : missingRequiredProperties) {
            var path = PropertyPath.from(propName, root.getJavaType());
            selections.add(JpaUtils.toExpressionRecursively(root, path, true).alias(propName));
        }

        return selections;
    }

    //todo generics?
    public static <P> List<P> processResult(Method method, AppProjectionReturnedType retType,
                                            Object result,
                                            RepositoryMetadata repositoryMetadata,
                                            ProjectionFactory projectionFactory) {
        var jpaQueryMethod = new AppProjectingQueryMethod(method, repositoryMetadata, projectionFactory);

        var resultProcessor = new ResultProcessor(jpaQueryMethod, projectionFactory, retType);

        return resultProcessor.processResult(result, new TupleConverter(retType));

    }

    public static <E> List<Selection<?>> createSelections(AppProjectionReturnedType retType, Root<E> root,
                                                          CriteriaQuery<?> query,
                                                          CriteriaBuilder criteriaBuilder,
                                                          Map<String, EvaluatedPropertyParams> evaluatedPropertiesParams) {
        return createSelections(retType, root, query, criteriaBuilder, Collections.emptyList(), evaluatedPropertiesParams);
    }
}
