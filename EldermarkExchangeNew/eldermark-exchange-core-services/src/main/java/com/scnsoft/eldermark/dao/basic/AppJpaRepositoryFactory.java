package com.scnsoft.eldermark.dao.basic;

import com.scnsoft.eldermark.dao.basic.interceptor.TupleProjectionWithCollectionsAccessingMethodInterceptorFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

public class AppJpaRepositoryFactory extends JpaRepositoryFactory {

    private ClassLoader classLoader;
    private BeanFactory beanFactory;

    public AppJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                    EntityManager entityManager) {

        var repository = super.getTargetRepository(information, entityManager);

        //pass additional properties to repository implementations
        if (repository instanceof ProjectionFactoryAware) {
            ((ProjectionFactoryAware) repository).setProjectionFactory(
                    getProjectionFactoryForRepository(beanFactory, classLoader, information, entityManager)
            );
        }

        if (repository instanceof RepositoryInformationAware) {
            ((RepositoryInformationAware) repository).setRepositoryInformation(information);
        }

        return repository;
    }

    protected ProjectionFactory getProjectionFactoryForRepository(BeanFactory beanFactory,
                                                                  ClassLoader classLoader,
                                                                  RepositoryInformation information,
                                                                  EntityManager entityManager) {
        var projectionFactory = new AppProjectionFactory();

        var factory = new TupleProjectionWithCollectionsAccessingMethodInterceptorFactory(entityManager,
                getEntityInformation(information.getDomainType()));
        factory.setProjectionFactory(projectionFactory);

        projectionFactory.registerMethodInvokerFactory(factory);

        projectionFactory.setBeanClassLoader(classLoader);
        projectionFactory.setBeanFactory(beanFactory);

        return projectionFactory;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return AppJpaRepositoryImpl.class;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        super.setBeanClassLoader(classLoader);
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }
}
