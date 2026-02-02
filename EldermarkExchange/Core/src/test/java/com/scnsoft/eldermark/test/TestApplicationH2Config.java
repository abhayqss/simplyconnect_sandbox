package com.scnsoft.eldermark.test;

import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.DatabasesServiceImpl;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.ResidentServiceImpl;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.carecoordination.EventTypeService;
import com.scnsoft.eldermark.services.consana.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateType;
import org.apache.commons.dbcp.BasicDataSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import javax.xml.transform.Templates;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * @author phomal
 * Created on 2/13/17.
 */
@Configuration
@ComponentScan(basePackages = {
        // Can't scan "com.scnsoft.eldermark.services" - Spring context initialization fails due to missing properties
        "com.scnsoft.eldermark.services.merging", "com.scnsoft.eldermark.dao", "com.scnsoft.eldermark.dao.password",
        "com.scnsoft.eldermark.services.cda", "com.scnsoft.eldermark.services.consol", "com.scnsoft.eldermark.services.ccd",
        "com.scnsoft.eldermark.services.beans"

})
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.scnsoft.eldermark.dao")
@PropertySource({"classpath:datasource-test-h2.properties", "classpath:document-test.properties", "classpath:application-test.properties"})
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

    @Bean
    public ResidentService residentService() {
        final ResidentService residentService = new ResidentServiceImpl();
        beanFactory.autowireBean(residentService);
        return residentService;
    }

    @Bean
    public DatabasesService databasesService(final DatabasesDao dao) {
        final DatabasesService databasesService = new DatabasesServiceImpl(dao);
        beanFactory.autowireBean(databasesService);
        return databasesService;
    }

    @Bean
    public ResidentUpdateQueueProducer residentUpdateQueueProducer() {
        return new ResidentUpdateQueueProducer() {
            @Override
            public boolean putToResidentUpdateQueue(ResidentUpdateQueueDto residentUpdateDto) {
                return false;
            }

            @Override
            public boolean putToResidentUpdateQueue(Long residentId, Set<ResidentUpdateType> updateTypes) {
                return false;
            }

            @Override
            public boolean putToResidentUpdateQueue(Long residentId, ResidentUpdateType updateType) {
                return false;
            }
        };
    }

    // ============== workaround for autowiring services from not scanned packages BEGIN ==================
    @Bean
    public EventService eventService() {
        return Mockito.mock(EventService.class);
    }

    @Bean
    public PatientFacade patientFacade() {
        return Mockito.mock(PatientFacade.class);
    }

    @Bean
    public EventTypeService eventTypeService() {
        return Mockito.mock(EventTypeService.class);
    }

    @Bean
    public Templates xslTemplates() {
        return Mockito.mock(Templates.class);
    }

    @Bean
    public DocumentFacade documentFacade() {
        return Mockito.mock(DocumentFacade.class);
    }
    // ============== workaround for autowiring services from not scanned packages END ==================
}
