package com.scnsoft.eldermark.dao.basic.interceptor;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.basic.ProjectionFactoryAware;
import com.scnsoft.eldermark.util.StreamUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.projection.Accessor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This custom method interceptor will be used instead of Spring's {@link org.springframework.data.projection.MapAccessingMethodInterceptor}
 * inside {@link org.springframework.data.projection.ProxyProjectionFactory}.
 * <p>
 * Spring's MapAccessingMethodInterceptor just delegates method invocations to underlying Map.
 * Our enhancement is that we distinguish ordinary properties calls with Collection and Map property calls.
 * In case of ordinary calls we will get data from underlying Map just as Spring does.
 * In case of Collection or Map calls we will load data from DB and save it in a separate Map for future calls
 * (just the same way as Lazy load works)
 * <p>
 * In order to load collection properties from database we will use entityManager, domain class and id attributes.
 * It is required, that all id attributes are present in underlying source Map so that id of specific entity can be retrieved
 *
 * TODO create proxies for loaded collection properties
 */

//toso shorter name
class TupleProjectionWithCollectionsAccessingMethodInterceptor implements MethodInterceptor, ProjectionFactoryAware {

    private final Map<String, Object> propertiesMap;
    private final EntityManager entityManager;
    private final JpaEntityInformation<?, ?> entityInformation;
    private ProjectionFactory projectionFactory;

    private final Map<String, Object> collectionPropertiesMap;


    public TupleProjectionWithCollectionsAccessingMethodInterceptor(Map<String, Object> propertiesMap, EntityManager entityManager,
                                                                    JpaEntityInformation<?, ?> entityInformation) {
        Objects.requireNonNull(propertiesMap);
        Objects.requireNonNull(entityManager);
        Objects.requireNonNull(entityInformation);

        this.propertiesMap = propertiesMap;
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;

        collectionPropertiesMap = new ConcurrentHashMap<>();
    }

    private void requireIdAttributesAreInMap(Map<String, Object> propertiesMap, JpaEntityInformation<?, ?> entityInformation) {
        var missingKeys = StreamUtils.stream(entityInformation.getIdAttributeNames())
                .filter(idName -> !propertiesMap.containsKey(idName))
                .collect(Collectors.toSet());

        if (missingKeys.size() > 0) {
            throw new IllegalStateException("Missing the following id properties in property map: " + missingKeys.toString()
                    + ". Perhaps query was distinct");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(@SuppressWarnings("null") MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        if (ReflectionUtils.isObjectMethod(method)) {
            return invocation.proceed();
        }

        Accessor accessor = new Accessor(method);

        var sourceMap = propertiesMap;

        if (Collection.class.isAssignableFrom(method.getReturnType())) {
            if (accessor.isGetter() && !collectionPropertiesMap.containsKey(accessor.getPropertyName())) {
                loadCollectionProperty(method, accessor.getPropertyName());
            }
            sourceMap = collectionPropertiesMap;
        }

        if (Map.class.isAssignableFrom(method.getReturnType())) {
            System.out.println("requested map property");
            if (accessor.isGetter()) {
                //todo load
            }
            sourceMap = collectionPropertiesMap;
            throw new NotImplementedException("Map properties are not implemented yet");
        }

        if (accessor.isGetter()) {
            return sourceMap.get(accessor.getPropertyName());
        } else if (accessor.isSetter()) {
            sourceMap.put(accessor.getPropertyName(), invocation.getArguments()[0]);
            return null;
        }

        throw new IllegalStateException("Should never get here!");
    }

    private void loadCollectionProperty(Method method, String propertyName) {
        requireIdAttributesAreInMap(propertiesMap, entityInformation);

        var actualReturnType = ClassTypeInformation.fromReturnTypeOf(method);
        var actualGenericType = actualReturnType.getActualType();
        if (actualGenericType.getType().isInterface()) {
            throw new UnsupportedOperationException("Projections in collection properties are not supported yet");
        }

        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery();
        var root = query.from(entityInformation.getJavaType());

        var idPredicates = new ArrayList<Predicate>();
        for (String idName : entityInformation.getIdAttributeNames()) {
            idPredicates.add(cb.equal(root.get(idName), propertiesMap.get(idName)));
        }

        query.where(idPredicates.toArray(new Predicate[0]));

        var path = PropertyPath.from(propertyName, entityInformation.getJavaType());
        if (!path.getLeafProperty().getTypeInformation().equals(ClassTypeInformation.fromReturnTypeOf(method))) {
            //todo proxy results and support projections
            throw new UnsupportedOperationException("Projections in collection properties are not supported yet: " + path.toDotPath());
        }
        query.select(JpaUtils.toExpressionRecursively(root, path, true, JoinType.INNER));

        var result = entityManager.createQuery(query).getResultList();

        collectionPropertiesMap.put(propertyName, result);
    }

    @Override
    public void setProjectionFactory(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }
}
