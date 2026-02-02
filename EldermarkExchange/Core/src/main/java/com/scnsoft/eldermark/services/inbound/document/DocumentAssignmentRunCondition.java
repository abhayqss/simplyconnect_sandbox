package com.scnsoft.eldermark.services.inbound.document;

import com.scnsoft.eldermark.services.inbound.InboundFilesServiceRunCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DocumentAssignmentRunCondition extends InboundFilesServiceRunCondition {

    @Override
    protected boolean isIntegrationEnabled(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return isAssignmentEnabled(context);
    }

    private boolean isAssignmentEnabled(ConditionContext context) {
        return false; //integration is disabled because the only use case is Qualifacts integration, which is disabled.
    }
}
