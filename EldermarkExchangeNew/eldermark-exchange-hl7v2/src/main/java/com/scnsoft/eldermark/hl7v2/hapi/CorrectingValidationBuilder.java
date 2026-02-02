package com.scnsoft.eldermark.hl7v2.hapi;

import ca.uhn.hl7v2.validation.builder.support.DefaultValidationBuilder;

public abstract class CorrectingValidationBuilder extends DefaultValidationBuilder {

    @Override
    protected void configure() {
        //correctors should come before original rules in order to me applied before validation
        addCorrections();

        super.configure();
    }

    protected abstract void addCorrections();
}
