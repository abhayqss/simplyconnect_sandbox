package com.scnsoft.eldermark.api.shared.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The string has to be a well-formed phone number.
 *
 * <p>
 * The supported type is {@code CharSequence}. {@code null} or empty string is considered valid.
 * </p>
 *
 * @author phomal
 * Created on 6/23/2017.
 */
@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {

    String message() default "{com.scnsoft.eldermark.api.shared.validation.Phone.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
