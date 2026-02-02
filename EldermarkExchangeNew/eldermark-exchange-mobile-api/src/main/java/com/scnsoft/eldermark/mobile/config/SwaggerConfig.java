package com.scnsoft.eldermark.mobile.config;

import com.fasterxml.classmate.TypeResolver;
import com.scnsoft.eldermark.mobile.request.MobileRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
//todo configure
public class SwaggerConfig {

    @Value("${swagger.enabled}")
    private boolean enableSwagger;


    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket mobileApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enableSwagger)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.scnsoft.eldermark"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
//                .directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)),
                        newRule(Pageable.class, pageableMixin()))
                .useDefaultResponseMessages(false)
                /*.globalResponses(HttpMethod.GET,
                        singletonList(new ResponseBuilder()
                                .code("500")
                                .description("500 message")
                                .representation(MediaType.TEXT_XML)
                                .apply(r ->
                                        r.model(m ->
                                                m.referenceModel(ref ->
                                                        ref.key(k ->
                                                                k.qualifiedModelName(q ->
                                                                        q.namespace("some:namespace")
                                                                                .name("ERROR"))))))
                                .build()))*/
                .securitySchemes(singletonList(jwtToken()))
                .securityContexts(singletonList(securityContext()))
                .enableUrlTemplating(true)
                .globalRequestParameters(Arrays.asList(
                        new springfox.documentation.builders.RequestParameterBuilder()
                                .name(MobileRequestContext.API_SUB_VERSION_HEADER)
                                .description("Description")
                                .in(ParameterType.HEADER)
                                .required(false)
                                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                .build(),
                        new springfox.documentation.builders.RequestParameterBuilder()
                                .name(MobileRequestContext.PLATFORM_HEADER)
                                .description("Description")
                                .in(ParameterType.HEADER)
                                .required(false)
                                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                .build()
                ));
//                .tags(new Tag("Simply Connect Mobile applications API", "Simply Connect Mobile applications API"))
//                .additionalModels(typeResolver.resolve(LoginDto.class));
    }

    private ApiKey jwtToken() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/anyPath.*"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return singletonList(
                new SecurityReference("Authorization", authorizationScopes));
    }

//    @Bean
//    SecurityConfiguration security() {
//        return SecurityConfigurationBuilder.builder()
//                .clientId("test-app-client-id")
//                .clientSecret("test-app-client-secret")
//                .realm("test-app-realm")
//                .appName("test-app")
//                .scopeSeparator(",")
//                .additionalQueryStringParams(null)
//                .useBasicAuthenticationWithAccessCodeGrant(false)
//                .enableCsrfSupport(false)
//                .build();
//    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .showCommonExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null)
                .build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Simply Connect mobile application API")
                .description("Simply Connect mobile application API.\n### Terms of Service\nIn order to access the SimplyConnect API you must have written consent from Simply Connect HIE. Any other use of this API is prohibited. Please contact us.")
//                .termsOfServiceUrl("In order to access the SimplyConnect API you must have written consent from Simply Connect HIE. Any other use of this API is prohibited. Please contact us.")
                .contact(ApiInfo.DEFAULT_CONTACT)
                .version("1.0")
                .build();
    }

    private Type pageableMixin() {
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(
                        String.format("%s.generated.%s",
                                Pageable.class.getPackage().getName(),
                                Pageable.class.getSimpleName()))
                .property(propertyBuilder -> property(propertyBuilder, Integer.class, "page"))
                .property(propertyBuilder -> property(propertyBuilder, Integer.class, "size"))
                .property(propertyBuilder -> property(propertyBuilder, String.class, "sort"))
                .build();
    }

    private AlternateTypePropertyBuilder property(AlternateTypePropertyBuilder builder, Class<?> type, String name) {
        return builder
                .name(name)
                .type(type)
                .canRead(true)
                .canWrite(true);
    }
}
