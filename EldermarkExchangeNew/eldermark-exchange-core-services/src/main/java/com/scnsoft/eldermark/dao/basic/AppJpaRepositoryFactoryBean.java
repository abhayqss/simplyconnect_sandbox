package com.scnsoft.eldermark.dao.basic;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;


public class AppJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {

    private EntityPathResolver entityPathResolver;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    /**
     * Creates a new {@link AppJpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public AppJpaRepositoryFactoryBean(Class repositoryInterface) {
        super(repositoryInterface);
    }

    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        var jpaRepositoryFactory = new AppJpaRepositoryFactory(entityManager);

        //pass entityPathResolver and escapeCharacter to factory in the same way as JpaRepositoryFactoryBean does
        jpaRepositoryFactory.setEntityPathResolver(entityPathResolver);
        jpaRepositoryFactory.setEscapeCharacter(escapeCharacter);

        return jpaRepositoryFactory;
    }

    /**
     * [copypaste] from JpaRepositoryFactoryBean
     * <p>
     * Configures the {@link EntityPathResolver} to be used. Will expect a canonical bean to be present but fallback to
     * {@link SimpleEntityPathResolver#INSTANCE} in case none is available.
     *
     * @param resolver must not be {@literal null}.
     */
    @Autowired
    public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
        super.setEntityPathResolver(resolver);

        this.entityPathResolver = resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
    }

    /**
     * [copypaste] from JpaRepositoryFactoryBean
     */
    public void setEscapeCharacter(char escapeCharacter) {
        super.setEscapeCharacter(escapeCharacter);

        this.escapeCharacter = EscapeCharacter.of(escapeCharacter);
    }
}
