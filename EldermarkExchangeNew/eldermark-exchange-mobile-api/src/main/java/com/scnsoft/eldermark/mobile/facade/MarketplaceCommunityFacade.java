package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.community.marketplace.FeaturedServiceProviderDto;

import java.util.List;

public interface MarketplaceCommunityFacade {

    List<FeaturedServiceProviderDto> fetchFeaturedServiceProviders(Long communityId);

    Boolean canViewList();
}
