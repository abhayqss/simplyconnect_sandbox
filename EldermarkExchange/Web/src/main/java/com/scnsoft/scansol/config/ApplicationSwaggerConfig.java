package com.scnsoft.scansol.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * @author phomal
 * Created on 1/9/2018.
 */
@EnableSwagger2
public class ApplicationSwaggerConfig {

    Logger logger = Logger.getLogger(ApplicationSwaggerConfig.class.getName());

    @Value("${swagger.enabled.scansol}")
    private boolean isEnabled;

    @Bean
    public Docket api() {
        if (isEnabled) {
            logger.info("Initializing Swagger and SpringFox for Scan Solutions API. Swagger version: 2.0");
        } else {
            logger.info("Swagger and SpringFox for Scan Solution API are disabled.");
        }

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(isEnabled)
                .apiInfo(apiInfo())
                .consumes(Collections.singleton(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .produces(Collections.singleton(MediaType.APPLICATION_JSON_VALUE))
                .protocols(new HashSet<String>(Arrays.asList("http", "https")))
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.scnsoft.scansol.web.controller")).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Simply Connect Web API",
                "External API for Scan Solution",
                "unknown",
                "In order to access the Simply Connect Web API you must have written consent from Simply Connect HIE. Any other use of this API is prohibited. Please contact us.",
                ApiInfo.DEFAULT_CONTACT,
                null,
                null,
                new ArrayList<VendorExtension>()
        );
    }

}
