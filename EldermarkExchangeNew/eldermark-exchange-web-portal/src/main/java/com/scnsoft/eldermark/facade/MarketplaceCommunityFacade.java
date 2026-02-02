package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.FeaturedServiceProviderFilter;
import com.scnsoft.eldermark.beans.MarketplaceFilter;
import com.scnsoft.eldermark.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MarketplaceCommunityFacade {

    Page<MarketplaceCommunitySummaryDto> find(MarketplaceFilter filter, Pageable pageRequest);

    CommunityWithAddressDetailsDto findByCommunityId(Long communityId, Long referralClientId);

    Page<MarketplaceCommunitySummaryDto> findPartners(Long communityId, MarketplaceFilter filter, Pageable pageRequest);

    Page<MarketplaceSavedCommunitySummaryDto> findSavedMarketplaces(Pageable pageable);

    void addSavedMarketplaceByCommunityId(Long communityId);

    void removeSavedMarketplaceByCommunityId(Long communityId);

    List<MarketplaceCommunityLocationListItemDto> findLocations(MarketplaceFilter filter);

    MarketplaceCommunityLocationDetailsDto findLocationDetails(Long communityId);

    Page<FeaturedServiceProviderDto> fetchFeaturedServiceProviders(
            FeaturedServiceProviderFilter filter, Pageable pageable
    );

    boolean existsInNetworkMarketplaceAccessibleCommunities();
}
