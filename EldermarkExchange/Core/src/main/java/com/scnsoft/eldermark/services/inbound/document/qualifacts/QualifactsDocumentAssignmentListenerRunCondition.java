package com.scnsoft.eldermark.services.inbound.document.qualifacts;

import com.scnsoft.eldermark.services.inbound.document.DocumentAssignmentListenerRunCondition;
import com.scnsoft.eldermark.services.integration.qualifacts.QualifactsIntegrationEnabledCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class QualifactsDocumentAssignmentListenerRunCondition extends DocumentAssignmentListenerRunCondition {

    @Override
    protected boolean isListenerEnabled(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return new QualifactsIntegrationEnabledCondition().matches(context, metadata);
    }
}
