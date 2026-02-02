package com.scnsoft.eldermark.service.pointclickcare;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class PccIntegrationOrPatientMatchEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        var pccEnabled = conditionContext.getEnvironment().getProperty("pcc.integration.enabled");
        var patientMatch = conditionContext.getEnvironment().getProperty("pcc.patientMatch.enabled");

        return "true".equals(pccEnabled) || "true".equals(patientMatch);
    }
}
