package com.scnsoft.eldermark.dao.basic.interceptor;

import com.scnsoft.eldermark.dao.basic.ProjectionFactoryAware;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.projection.MethodInterceptorFactory;
import org.springframework.data.projection.ProjectionFactory;

import javax.persistence.EntityManager;
import java.util.Map;

public class TupleProjectionWithCollectionsAccessingMethodInterceptorFactory
        implements MethodInterceptorFactory, ProjectionFactoryAware {

    private final EntityManager entityManager;
    private final JpaEntityInformation<?, ?> entityInformation;
    private ProjectionFactory projectionFactory;

    public TupleProjectionWithCollectionsAccessingMethodInterceptorFactory(EntityManager entityManager,
                                                                           JpaEntityInformation<?, ?> entityInformation){
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.projection.MethodInterceptorFactory#createMethodInterceptor(java.lang.Object, java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public MethodInterceptor createMethodInterceptor(Object source, Class<?> targetType) {
        var interceptor = new TupleProjectionWithCollectionsAccessingMethodInterceptor((Map<String, Object>) source,
                entityManager, entityInformation);
        interceptor.setProjectionFactory(projectionFactory);
        return interceptor;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.projection.MethodInterceptorFactory#supports(java.lang.Object, java.lang.Class)
     */
    @Override
    public boolean supports(Object source, Class<?> targetType) {
        return Map.class.isInstance(source);
    }

    @Override
    public void setProjectionFactory(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }
}