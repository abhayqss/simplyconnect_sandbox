package com.scnsoft.eldermark.services.inbound.document;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public abstract class DocumentAssignmentListenerRunCondition extends DocumentAssignmentRunCondition {

    @Override
    protected boolean isIntegrationEnabled(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return super.isIntegrationEnabled(context, metadata) && isListenerEnabled(context, metadata);
    }

    protected abstract boolean isListenerEnabled(ConditionContext context, AnnotatedTypeMetadata metadata);
}
