package com.scnsoft.eldermark.api.shared.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validate that the character sequence (e.g. string) is a valid UUID using a regular expression.
 *
 * @author phomal
 * Created on 2/14/2018.
 */
public class UuidValidator implements ConstraintValidator<Uuid, CharSequence> {

    @Override
    public void initialize(Uuid paramA) {
    }

    @Override
    public boolean isValid(CharSequence uuid, ConstraintValidatorContext ctx) {
        if (uuid == null) {
            return true;
        }
        return Pattern.matches("^[a-fA-F0-9]{8}-([a-fA-F0-9]{4}-){3}[a-fA-F0-9]{12}$", uuid);
    }

}