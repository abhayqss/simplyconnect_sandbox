package com.scnsoft.eldermark.service.security;

public interface MarketplaceCommunitySecurityService {

    boolean canConfigure(Long communityId);

    boolean canConfigureInOrganization(Long organizationId);

    boolean canViewList();

    boolean canViewByCommunityId(Long communityId);

    boolean canViewPartnerProviders(Long communityId);

    boolean canViewFeaturedPartnerProviders(Long communityId);

    boolean canEditFeaturedPartnerProviders(Long communityId, Long organizationId);
}
