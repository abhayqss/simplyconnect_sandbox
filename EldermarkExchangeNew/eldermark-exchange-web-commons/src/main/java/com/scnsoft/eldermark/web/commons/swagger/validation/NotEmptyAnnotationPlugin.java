package com.scnsoft.eldermark.web.commons.swagger.validation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

import static springfox.bean.validators.plugins.Validators.annotationFromBean;
import static springfox.bean.validators.plugins.Validators.annotationFromField;

@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true")
@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class NotEmptyAnnotationPlugin implements ModelPropertyBuilderPlugin {

    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<NotEmpty> notEmpty = extractAnnotation(context);
        notEmpty.ifPresent((notEmpty1) -> {
            context.getSpecificationBuilder().required(true);
//            AllowableRangeValues allowableRangeValues = new AllowableRangeValues("1", false, null, false);
//            context.getBuilder().allowableValues(allowableRangeValues);
            context.getSpecificationBuilder().stringFacet((s) -> {
                s.minLength(1);
            });

            context.getSpecificationBuilder().collectionFacet((s) -> {
                s.minItems(1);
            });
        });
    }

    Optional<NotEmpty> extractAnnotation(ModelPropertyContext context) {
        return annotationFromBean(context, NotEmpty.class)
                .or(() -> annotationFromField(context, NotEmpty.class));
    }
}