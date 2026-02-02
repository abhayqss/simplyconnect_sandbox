package com.scnsoft.eldermark.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This bean is initialized before datasource and ensures that application is going to connect in-memory database (H2) for testing.
 * <br/>
 *
 * @author phomal
 * Created on 3/14/2018.
 */
@Component
@Profile("h2")
public class IntegrationTestPostConstruct implements PostConstructEnvironmentValidation {

    @Value("${datasource.url}")
    private String dbUrl;

    public IntegrationTestPostConstruct() {}

    @Override
    @PostConstruct
    public void init() {
        if (StringUtils.isNotEmpty(dbUrl) && !StringUtils.startsWithIgnoreCase(dbUrl, "jdbc:h2:mem:")) {
            throw new BeanInitializationException("Running tests against real database is prohibited (due to a risk of losing data). You can either skip tests or repackage the application with another Maven profile (-P H2) that is intended for integration testing. JDBC connection URL is \"" + dbUrl + "\"");
        }
    }

}
