package com.scnsoft.eldermark.hl7v2.hapi;

import ca.uhn.hl7v2.validation.ValidationException;
import ca.uhn.hl7v2.validation.impl.AbstractPrimitiveTypeRule;

public abstract class PrimitiveTypeCorrector extends AbstractPrimitiveTypeRule {

    @Override
    public ValidationException[] apply(String value) {
        return new ValidationException[0];
    }

    @Override
    public String correct(String value) {
        return doCorrect(value);
    }

    protected abstract String doCorrect(String value);
}
