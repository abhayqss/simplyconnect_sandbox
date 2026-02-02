package com.scnsoft.eldermark.services.inbound.marco;

import com.scnsoft.eldermark.services.inbound.InboundFilesServiceRunCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MarcoInboundFilesServiceRunCondition extends InboundFilesServiceRunCondition {

    @Override
    protected boolean isIntegrationEnabled(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return false; //integration is disabled
    }
}
