package com.scnsoft.eldermark.api.shared.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The string has to be a well-formed social security number (SSN).
 *
 * <p>
 * The supported type is {@code CharSequence}. {@code null} or empty string is considered valid.
 * </p>
 *
 * @author phomal
 * Created on 6/23/2017.
 */
@Documented
@Constraint(validatedBy = SsnValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ssn {

    String message() default "{com.scnsoft.eldermark.api.shared.validation.Ssn.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
