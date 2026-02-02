package com.scnsoft.eldermark.services.inbound;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class InboundFilesServiceRunCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return inboundFilesEnabled(context) && isIntegrationEnabled(context, metadata);
    }

    private boolean inboundFilesEnabled(ConditionContext context) {
        return !isTrue(context, "inboundfiles.run.disable");
    }

    protected boolean isTrue(ConditionContext context, String prop) {
        return Boolean.TRUE.equals(getProperty(context, prop, Boolean.class));
    }

    protected <T> T getProperty(ConditionContext context, String prop, Class<T> tClass) {
        return context.getEnvironment().getProperty(prop, tClass);
    }

    protected boolean isIntegrationEnabled(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return true;
    }
}
