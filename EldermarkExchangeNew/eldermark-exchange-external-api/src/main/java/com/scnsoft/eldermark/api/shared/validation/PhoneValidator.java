package com.scnsoft.eldermark.api.shared.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validate that the character sequence (e.g. string) is a valid phone number using regular expressions.
 *
 * @author phomal
 * Created on 6/23/2017.
 */
public class PhoneValidator implements ConstraintValidator<Phone, CharSequence> {

    @Override
    public void initialize(Phone paramA) {
    }

    @Override
    public boolean isValid(CharSequence phone, ConstraintValidatorContext ctx) {
        if (StringUtils.isEmpty(phone)) {
            return true;
        }

        // phone numbers of format "+1234567890"
        if (Pattern.matches("\\+?\\d{7,15}", phone)) {
            return true;
        }
        // phone number with -, . or spaces
        else if (Pattern.matches("\\+?\\d{3}[-.\\s]?\\d{3}[-.\\s]?\\d{1,4}", phone)) {
            return true;
        }
        // +(777) 777-7777
        // phone number where area code is in braces ()
        else if (Pattern.matches("\\+?\\(\\d{3}\\) ?-? ?\\d{3} ?-? ?\\d{1,4}", phone)) {
            return true;
        }
        // phone number where area code is in braces () and is prepended with country code
        else if (Pattern.matches("\\+?\\d{3}\\(\\d{2}\\) ?-? ?\\d{3} ?-? ?\\d{1,4}", phone)) {
            return true;
        } else {
            //return false if nothing matches the input
            return false;
        }
    }

}