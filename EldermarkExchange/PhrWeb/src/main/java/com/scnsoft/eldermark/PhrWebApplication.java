package com.scnsoft.eldermark;

import com.scnsoft.eldermark.config.PersistenceConfig;
import com.scnsoft.eldermark.shared.ApiSharedConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;


@SpringBootApplication(
        scanBasePackages = {"com.scnsoft.eldermark.config", "com.scnsoft.eldermark.service", "com.scnsoft.eldermark.web"},
        exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class, JmsAutoConfiguration.class})
@Import({ApiSharedConfiguration.class, PersistenceConfig.class})
@ImportResource({"classpath*:spring/applicationContext.xml"})
public class PhrWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhrWebApplication.class, args);
    }
}
