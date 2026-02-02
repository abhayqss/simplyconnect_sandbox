package com.scnsoft.eldermark.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This bean is initialized before datasource and ensures that application is going to connect real database (MS SQL Server).
 * <br/>
 *
 * @author phomal
 * Created on 3/14/2018.
 */
@Component
@Profile("!h2")
public class MainPostConstruct implements PostConstructEnvironmentValidation {

    @Value("${datasource.url}")
    private String dbUrl;

    public MainPostConstruct() {}

    @Override
    @PostConstruct
    public void init() {
        if (StringUtils.isNotEmpty(dbUrl) && !StringUtils.startsWithIgnoreCase(dbUrl, "jdbc:sqlserver:")) {
            throw new BeanInitializationException("The application is not intended to work with databases different from MS SQL Server. Application should be repackaged with another Maven profile (-P Local | -P Test | -P Prod2) before running. JDBC connection URL is \"" + dbUrl + "\"");
        }
    }

}
