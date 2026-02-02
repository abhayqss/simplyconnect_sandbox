package com.scnsoft.eldermark.api.shared.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 * The supported type is {@code CharSequence}. {@code null} or empty string is considered valid.
 * </p>
 *
 * @author phomal
 * Created on 8/3/2017.
 */
@Documented
@Constraint(validatedBy = ExchangeLoginValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExchangeLogin {

    String message() default "{com.scnsoft.eldermark.api.shared.validation.ExchangeLogin.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
