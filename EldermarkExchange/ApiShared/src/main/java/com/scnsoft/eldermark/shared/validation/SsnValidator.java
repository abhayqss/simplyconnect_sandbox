package com.scnsoft.eldermark.shared.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validate that the character sequence (e.g. string) is a valid SSN.
 *
 * @author phomal
 * Created on 6/23/2017.
 */
public class SsnValidator implements ConstraintValidator<Ssn, CharSequence> {

    @Override
    public void initialize(Ssn paramA) {
    }

    @Override
    public boolean isValid(CharSequence ssn, ConstraintValidatorContext ctx) {
        if (StringUtils.isEmpty(ssn)) {
            return true;
        }
        return com.scnsoft.eldermark.shared.service.validation.SsnValidator.isValidSsn(ssn);
    }

}