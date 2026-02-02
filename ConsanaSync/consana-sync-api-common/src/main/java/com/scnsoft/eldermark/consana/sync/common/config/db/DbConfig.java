package com.scnsoft.eldermark.consana.sync.common.config.db;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(basePackages = {"com.scnsoft.eldermark.consana.sync.common.services.db"})
@PropertySources({
        @PropertySource("classpath:config/db/db.properties"),
        @PropertySource("classpath:config/db/db-${spring.profiles.active}.properties")
})
public class DbConfig {
}
