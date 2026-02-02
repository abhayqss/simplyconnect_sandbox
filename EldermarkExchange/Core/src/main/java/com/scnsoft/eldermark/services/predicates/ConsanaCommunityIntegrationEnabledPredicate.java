package com.scnsoft.eldermark.services.predicates;

import com.google.common.base.Predicate;
import com.scnsoft.eldermark.entity.Organization;
import org.springframework.stereotype.Component;

@Component
public class ConsanaCommunityIntegrationEnabledPredicate implements Predicate<Organization> {

    @Override
    public boolean apply(Organization organization) {
        return organization != null && Boolean.TRUE.equals(organization.getConsanaIntegrationEnabled());
    }
}
