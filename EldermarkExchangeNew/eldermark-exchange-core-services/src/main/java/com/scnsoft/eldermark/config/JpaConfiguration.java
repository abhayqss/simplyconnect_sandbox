package com.scnsoft.eldermark.config;

import com.scnsoft.eldermark.dao.basic.AppJpaRepositoryFactoryBean;
import com.scnsoft.eldermark.jpa.DelegatingPlatformTransactionManager;
import com.scnsoft.eldermark.jpa.OpenKeyTransactionListener;
import com.scnsoft.eldermark.jpa.TransactionListener;
import com.scnsoft.eldermark.service.SymmetricKeySqlServerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import java.util.List;

/**
 * This configuration configures Jpa in the app The following points apply:
 * <ol>
 *     <li>
 *         Custom repository factory {@link AppJpaRepositoryFactoryBean} is used.
 *         Responsibilities of the factory is to create
 *         {@link com.scnsoft.eldermark.dao.basic.AppJpaRepositoryImpl}
 *         repository instances, which supports specifications with projections
 *      </li>
 *      <li>
 *          Added BeanPostProcessor which proxies default PlatformTransactionManager bean with
 *          our custom {@link DelegatingPlatformTransactionManager}. It allows to inject
 *          {@link TransactionListener} for PlatformTransactionManager methods.
 *      </li>
 *      <li>
 *          {@link OpenKeyTransactionListener} is added to {@link DelegatingPlatformTransactionManager},
 *          which is opens symmetric key after new transaction is created via
 *          {@link PlatformTransactionManager#getTransaction(TransactionDefinition)} call.
 *          This allows automatic key openning both for @Transactional methods and for transactions
 *          created programatically.
 * @see com.scnsoft.eldermark.h2.symmetric_key.OpenKeyIT which verifies key openning
 *      </li>
 * </ol>
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.scnsoft.eldermark", repositoryFactoryBeanClass = AppJpaRepositoryFactoryBean.class)
@PropertySources({
        @PropertySource("classpath:config/jpa/jpa.properties"),
        @PropertySource("classpath:config/jpa/jpa-${spring.profiles.active}.properties"),
})
public class JpaConfiguration {

    @Bean
    public PlatformTransactionManagerWrappingBeanPostProcessor platformTransactionManagerWrappingBeanPostProcessor(
            List<TransactionListener> transactionListeners){
        return new PlatformTransactionManagerWrappingBeanPostProcessor(transactionListeners);
    }


    @Bean
    public TransactionListener openKeyTransactionListener(SymmetricKeySqlServerService symmetricKeySqlServerService) {
        return new OpenKeyTransactionListener(symmetricKeySqlServerService);
    }
}
