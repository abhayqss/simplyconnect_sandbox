package com.scnsoft.eldermark.service.security;

public interface OrganizationSecurityService {

    boolean canAdd();

    boolean canEdit(Long organizationId);

    boolean canEditFeatures(Long organizationId);

    boolean canConfigureMarketplace(Long organizationId);

    boolean canConfigureAffiliateRelationships(Long organizationId);

    boolean canViewList();

    boolean canView(Long organizationId);

    boolean canViewLogo(Long organizationId);
}
