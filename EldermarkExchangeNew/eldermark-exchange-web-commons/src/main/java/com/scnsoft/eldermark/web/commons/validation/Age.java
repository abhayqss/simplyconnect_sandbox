package com.scnsoft.eldermark.web.commons.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {AgeConstraintValidator.class})
public @interface Age {

    String message() default "Age doesn't meet requirements";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value() default AgeConstraintValidator.NO_DATA;

    AgeConstraintValidator.Rule[] rules() default {};
}
