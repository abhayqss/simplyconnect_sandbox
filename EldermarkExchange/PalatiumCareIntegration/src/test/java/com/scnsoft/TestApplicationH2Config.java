package com.scnsoft;

import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.DatabasesServiceImpl;
import com.scnsoft.service.PalCareLocationService;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Properties;

/**
 * @author mtsylko
 * Created on 8/17/18.
 */
@Configuration
@ComponentScan(basePackages = {"com.scnsoft.service"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "com.scnsoft.service.security.*"))
@EnableTransactionManagement
@EnableJpaRepositories
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource({"classpath:application-test-h2.properties"})
public class TestApplicationH2Config {

    @Value("${datasource.driverClassName}")
    private String driverClassName;

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.connection.charSet}")
    private String connectionCharset;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddlAuto;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    private static final String CONNECTION_INIT_SQLS =
            "CREATE ALIAS IF NOT EXISTS hash_string DETERMINISTIC FOR \"com.scnsoft.eldermark.shared.test.H2Function.sha256\";";

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setConnectionInitSqls(Collections.singleton(CONNECTION_INIT_SQLS));

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setJpaDialect(new HibernateJpaDialect());
        entityManagerFactoryBean.setPackagesToScan("com.scnsoft.eldermark.entity");

        entityManagerFactoryBean.setJpaProperties(hibProperties());

        return entityManagerFactoryBean;
    }

    private Properties hibProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.connection.charSet", connectionCharset);
        properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        properties.put("hibernate.jdbc.use_get_generated_keys", false);
        properties.put("hibernate.cache.use_second_level_cache", false);

        return properties;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "palCareLocationService")
    public PalCareLocationService palCareLocationService() {
        PalCareLocationService palCareLocationService = new PalCareLocationService();
        //beanFactory.autowireBean(palCareLocationService);
        return palCareLocationService;
    }

}

