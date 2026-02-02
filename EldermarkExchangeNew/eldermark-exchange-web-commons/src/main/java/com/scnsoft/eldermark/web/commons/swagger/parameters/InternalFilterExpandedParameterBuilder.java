package com.scnsoft.eldermark.web.commons.swagger.parameters;

import com.scnsoft.eldermark.annotations.InternalFilterParameter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import java.util.Optional;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER;

/**
 * Hide request parameters annotated with {@link InternalFilterParameter} inside ModelAttributes classes from Swagger-UI
 */
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true")
@Component
@Order(SWAGGER_PLUGIN_ORDER + 1)
public class InternalFilterExpandedParameterBuilder implements ExpandedParameterBuilderPlugin {

    @Override
    public void apply(ParameterExpansionContext context) {
        Optional<InternalFilterParameter> internalParameterOptional = context.findAnnotation(InternalFilterParameter.class);
        internalParameterOptional.ifPresent(internalParameter -> {
                    context.getRequestParameterBuilder().hidden(true);
                    context.getParameterBuilder().hidden(true);
                }
        );
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        //all documentation types
        return true;
    }
}
