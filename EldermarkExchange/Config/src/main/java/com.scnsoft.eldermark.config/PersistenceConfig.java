package com.scnsoft.eldermark.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Properties;

@Configuration
/*
[dbahachou, aduzhynskaya]
<!-- Apply security advice (order 1) at first
     then audit logging advice (order 2)
     and then apply transactional advice (order 3) -->
*/
@EnableTransactionManagement(order = 3)
@EnableJpaRepositories(basePackages = "com.scnsoft.eldermark.dao")
@PropertySource({"classpath:datasource.properties", "classpath:hibernate.properties", "classpath:document.properties"})
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class PersistenceConfig {

    @Value("${datasource.driverClassName}")
    private String driverClassName;

    @Value("${datasource.url}")
    private String dbUrl;

    @Value("${datasource.username}")
    private String dbUsername;

    @Value("${datasource.password}")
    private String dbPassword;

    @Value("${datasource.connectionInitSqls}")
    private String dbConnectionInitSqls;

    @Value("${datasource.testOnBorrow}")
    private boolean dbTestOnBorrow;

    @Value("${datasource.testOnReturn}")
    private boolean dbTestOnReturn;

    @Value("${datasource.testWhileIdle}")
    private boolean dbTestWhileIdle;

    @Value("${datasource.timeBetweenEvictionRunsMillis}")
    private long dbTimeBetweenEvictionRunsMillis;

    @Value("${datasource.numTestsPerEvictionRun}")
    private int dbNumTestsPerEvictionRun;

    @Value("${datasource.minEvictableIdleTimeMillis}")
    private long dbMinEvictableIdleTimeMillis;

    @Value("${datasource.validationQuery}")
    private String dbValidationQuery;

    // [mradzivonenka] datasource property to remove abandoned connections
    @Value("${datasource.removeAbandoned}")
    private boolean dbRemoveAbandoned;

    @Value("${datasource.removeAbandonedTimeout}")
    private int dbRemoveAbandonedTimeout;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.connection.charSet}")
    private String hibernateConnectionCharset;

    @Value("${hibernate.show_sql}")
    private boolean hibernateShowSql;

    @Value("${hibernate.format_sql}")
    private boolean hibernateFormatSql;
    /*
    @Value("${hibernate.cache.use_second_level_cache}")
    private boolean hibernateUseSecondLevelCache;

    @Value("${hibernate.cache.use_query_cache}")
    private boolean hibernateUseQueryCache;

    @Value("${hibernate.cache.region.factory_class}")
    private String hibernateCacheRegionFactoryClass;
    */
    @Value("${hibernate.jdbc.use_get_generated_keys}")
    private boolean hibernateUseGetGeneratedKeys;

    @Value("${hibernate.generate_statistics}")
    private boolean hibernateGenerateStatistics;

    // [Netkachev] CSP-551 Patient merge functionality implementation - fixed lucene indexes dir
    @Value("${lucene.indexes.dir}")
    private String luceneIndexesDir;


    // [phomal] CCN-838 Allow unlimited number of active connections
    private static final int MAX_ACTIVE = -1;

    private String hbm2ddlAuto = "none";
    private Database hibernateDatabase;

    @Autowired
    @SuppressWarnings("unused")
    private PostConstructEnvironmentValidation environmentValidation;

    /**
     * This initialization method sets {@code hbm2ddl.auto=create-drop} only if current database platform is H2 in-memory database.
     * <br/><br/>
     * {@code hbm2ddl.auto=update} will update the database schema. It is not recommended in production, see discussion at
     * <a href="https://stackoverflow.com/q/221379/1429387">Stack Overflow</a>.
     * <br/>
     * {@code hbm2ddl.auto=create-drop} will drop database (we'll lose everything) when the session factory is closed, and recreate the schema when it restarts.
     * So we really, REALLY don't want that in production.
     */
    @PostConstruct
    public void init() {
        if (StringUtils.startsWithIgnoreCase(dbUrl, "jdbc:h2:mem:")) {
            hbm2ddlAuto = "create-drop";
            hibernateDatabase = Database.H2;
        } else if (StringUtils.startsWithIgnoreCase(dbUrl, "jdbc:sqlserver:")) {
            hibernateDatabase = Database.SQL_SERVER;
        } else {
            throw new BeanInitializationException("JDBC connection string has unknown protocol: " + dbUrl + ". Can't infer database platform from JDBC URL automatically.");
        }
    }

    @Bean
    @Autowired
    public EntityManagerFactory entityManagerFactory(DataSource mainDataSource, JpaVendorAdapter vendorAdapter) {
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPackagesToScan("com.scnsoft.eldermark.entity", "com.scnsoft.eldermark.shared.ccd");
        factory.setDataSource(mainDataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(hibProperties());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean
    public DataSource dataSource() {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setConnectionInitSqls(Collections.singleton(dbConnectionInitSqls));
        dataSource.setMaxActive(MAX_ACTIVE);
        dataSource.setTestOnBorrow(dbTestOnBorrow);
        dataSource.setTestOnReturn(dbTestOnReturn);
        dataSource.setTestWhileIdle(dbTestWhileIdle);
        dataSource.setTimeBetweenEvictionRunsMillis(dbTimeBetweenEvictionRunsMillis);
        dataSource.setNumTestsPerEvictionRun(dbNumTestsPerEvictionRun);
        dataSource.setMinEvictableIdleTimeMillis(dbMinEvictableIdleTimeMillis);
        dataSource.setValidationQuery(dbValidationQuery);
        dataSource.setRemoveAbandoned(dbRemoveAbandoned);
        dataSource.setRemoveAbandonedTimeout(dbRemoveAbandonedTimeout);

        return dataSource;
    }

    private Properties hibProperties() {
        final Properties properties = new Properties();
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.show_sql", hibernateShowSql);
        properties.put("hibernate.format_sql", hibernateFormatSql);
        properties.put("hibernate.connection.charSet", hibernateConnectionCharset);
        properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);

        /* L2 cache is temporary disabled
        properties.put("hibernate.cache.use_second_level_cache", hibernateUseSecondLevelCache);
        properties.put("hibernate.cache.use_query_cache", hibernateUseQueryCache);
        properties.put("hibernate.cache.region.factory_class", hibernateCacheRegionFactoryClass);
        */
        properties.put("hibernate.jdbc.use_get_generated_keys", hibernateUseGetGeneratedKeys);
        properties.put("hibernate.generate_statistics", hibernateGenerateStatistics);

        // [Netkachev] patient match & merge functionality
        properties.put("hibernate.search.default.directory_provider", "filesystem");
        properties.put("hibernate.search.default.indexBase", luceneIndexesDir);

        return properties;
    }

    @Bean
    @Autowired
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory, DataSource mainDataSource) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setDataSource(mainDataSource);
        return transactionManager;
    }

    /* Hibernate 4
    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        final HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }

    @Bean
    @Autowired
    public LocalSessionFactoryBean sessionFactoryBean(DataSource mainDataSource) {
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(mainDataSource);
        sessionFactory.setPackagesToScan("com.scnsoft.eldermark");
        sessionFactory.setHibernateProperties(hibProperties());

        return sessionFactory;
    }*/

    @Bean
    public JpaVendorAdapter vendorAdapter() {
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(hibernateShowSql);
        vendorAdapter.setDatabasePlatform(hibernateDialect);
        vendorAdapter.setDatabase(hibernateDatabase);

        return vendorAdapter;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public PostConstructEnvironmentValidation getEnvironmentValidation() {
        return environmentValidation;
    }

    public void setEnvironmentValidation(PostConstructEnvironmentValidation environmentValidation) {
        this.environmentValidation = environmentValidation;
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
}