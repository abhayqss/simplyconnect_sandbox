package com.scnsoft.eldermark.services.beans;

import org.owasp.html.PolicyFactory;

public abstract class AbstractPolicyFactoryWrapper implements PolicyFactoryWrapper {

    private final PolicyFactory INSTANCE;

    public AbstractPolicyFactoryWrapper() {
        INSTANCE = initPolicyFactory();
    }

    protected abstract PolicyFactory initPolicyFactory();

    @Override
    public PolicyFactory getPolicyFactory() {
        return INSTANCE;
    }
}