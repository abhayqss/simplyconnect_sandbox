package com.scnsoft.eldermark.web.commons.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:config/web-commons.properties"),
        @PropertySource("classpath:config/web-commons-${spring.profiles.active}.properties"),
})
public class WebCommonsConfiguration {

}
