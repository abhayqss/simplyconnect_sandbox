package com.scnsoft.eldermark.services.task;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author phomal
 * Created on 2/19/2018.
 */
public class EmployeeRequestCleanerRunCondition implements ConfigurationCondition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return false; //request cleaner is working in new portal
//        final Boolean disableInvitationExpiration = context.getEnvironment().getProperty("invitation.expiration.disable", Boolean.class);
//        return !Boolean.TRUE.equals(disableInvitationExpiration);
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }
}
