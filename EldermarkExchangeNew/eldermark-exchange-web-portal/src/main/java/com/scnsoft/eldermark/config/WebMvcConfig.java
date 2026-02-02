package com.scnsoft.eldermark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final long MAX_AGE_SECS = 3600;

    @Value("#{'${cors.allowedOrigins}'.split(',')}")
    List<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping( "/**")
                .allowedMethods("*")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .maxAge(MAX_AGE_SECS)
                .allowCredentials(true);
    }

}
