package com.scnsoft.eldermark.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.context.ServletContextAware;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.logging.Logger;


/**
 * @author phomal
 * Created on 1/11/2018.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig implements ServletContextAware {

    Logger logger = Logger.getLogger(SwaggerConfig.class.getName());

    @Value("${swagger.enabled}")
    boolean isEnabled;

    @Value("${server.context-path}")
    String contextPath;

    private ServletContext servletContext;

    @Bean
    public Docket api() {
        if (isEnabled) {
            logger.info("Initializing Swagger and SpringFox. Swagger version: 2.0");
        } else {
            logger.info("Swagger and SpringFox are disabled.");
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(isEnabled)
                .apiInfo(apiInfo())
                .consumes(Collections.singleton(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .produces(Collections.singleton(MediaType.APPLICATION_JSON_VALUE))
                .protocols(new HashSet<>(Arrays.asList("https")))
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    public String getApplicationBasePath() {
                        return contextPath;
                    }
                })
                .useDefaultResponseMessages(false)
                .directModelSubstitute(Date.class, Long.class)
                .securitySchemes(Collections.singletonList(tokenSecurityScheme()))
                .securityContexts(Collections.singletonList(tokenSecurityContext()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.scnsoft.eldermark.web.controller"))
                .build();
    }

    private SecurityContext tokenSecurityContext() {
        AuthorizationScope[] authScopes = new AuthorizationScope[1];
        authScopes[0] = new AuthorizationScopeBuilder()
                .scope("global")
                .description("read/write access to PHR")
                .build();

        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(new SecurityReference("X-Auth-Token", authScopes)))
                .forPaths(Predicates.or(
                        PathSelectors.ant("/phr/**"),
                        PathSelectors.ant("/orgs/**"),
                        PathSelectors.ant("/communities/**"),
                        PathSelectors.ant("/employees/**"),
                        PathSelectors.ant("/residents/**"),
                        PathSelectors.ant("/nucleus/**"),
                        PathSelectors.ant("/events/**"),
                        PathSelectors.ant("/physicians/**")
                ))
                .build();
    }

    @Bean
    SecurityScheme tokenSecurityScheme() {
        return new ApiKey("X-Auth-Token", "X-Auth-Token", "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "External API",
                "SimplyConnect API for third-party apps\n### Terms of Service\nIn order to access the SimplyConnect API you must have written consent from Simply Connect HIE. Any other use of this API is prohibited. Please contact us.",
                "draft",
                null,
                ApiInfo.DEFAULT_CONTACT,
                null,
                null,
                new ArrayList<VendorExtension>()
        );
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
