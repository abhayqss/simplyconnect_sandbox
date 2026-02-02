package com.scnsoft.eldermark.api.shared.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Checks that the annotated character sequence is a valid <a href="https://en.wikipedia.org/wiki/Universally_unique_identifier">UUID</a>.<br/>
 * According to <a href="https://tools.ietf.org/html/rfc4122#section-3">Section 3 of RFC4122 </a>, a string representation of UUID must be 36 characters long - 32 hex digits + 4 dashes.
 *
 * <p>
 * The supported type is {@code CharSequence}. {@code null} is considered valid.
 * </p>
 *
 * @author phomal
 * Created on 2/14/2018.
 */
@Documented
@Constraint(validatedBy = UuidValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Uuid {

    String message() default "{com.scnsoft.eldermark.api.shared.validation.Uuid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
