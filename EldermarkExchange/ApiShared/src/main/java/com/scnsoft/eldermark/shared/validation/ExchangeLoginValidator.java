package com.scnsoft.eldermark.shared.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author phomal
 * Created on 8/3/2017.
 */
public class ExchangeLoginValidator implements ConstraintValidator<ExchangeLogin, CharSequence> {

    @Override
    public void initialize(ExchangeLogin paramA) {
    }

    @Override
    public boolean isValid(CharSequence login, ConstraintValidatorContext ctx) {
        if (StringUtils.isEmpty(login)) {
            return true;
        }

        return !login.toString().contains("/");
    }

}