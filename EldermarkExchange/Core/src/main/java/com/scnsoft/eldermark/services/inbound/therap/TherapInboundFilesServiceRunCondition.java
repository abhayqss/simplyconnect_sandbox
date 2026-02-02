package com.scnsoft.eldermark.services.inbound.therap;

import com.scnsoft.eldermark.services.inbound.InboundFilesServiceRunCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TherapInboundFilesServiceRunCondition extends InboundFilesServiceRunCondition {

    @Override
    protected boolean isIntegrationEnabled(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return false; //integration is disabled
    }
}
