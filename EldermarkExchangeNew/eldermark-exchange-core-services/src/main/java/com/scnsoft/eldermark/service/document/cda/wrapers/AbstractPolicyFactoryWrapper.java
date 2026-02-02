package com.scnsoft.eldermark.service.document.cda.wrapers;

import org.owasp.html.PolicyFactory;

public abstract class AbstractPolicyFactoryWrapper implements PolicyFactoryWrapper {

    private final PolicyFactory INSTANCE;

    public AbstractPolicyFactoryWrapper() {
        INSTANCE = initPolicyFactory();
    }

    @Override
    public PolicyFactory getPolicyFactory() {
        return INSTANCE;
    }

    protected abstract PolicyFactory initPolicyFactory();
}