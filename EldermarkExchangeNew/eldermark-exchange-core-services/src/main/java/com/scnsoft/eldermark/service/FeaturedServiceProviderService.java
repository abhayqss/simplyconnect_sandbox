package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider;

import java.util.Collection;
import java.util.List;

public interface FeaturedServiceProviderService {

    void saveAll(List<FeaturedServiceProvider> list);

    List<FeaturedServiceProvider> findAllByCommunityIdAndProviderIdIn(Long communityId, Collection<Long> providerIds);

    List<FeaturedServiceProvider> fetchServiceProvidersByCommunityId(Long communityId);

    List<FeaturedServiceProvider> findAllDiscoverableByCommunityId(Long communityId);

    void deleteByCommunityIdAndProviderIdIn(Long communityId, List<Long> providerIds);

    boolean isFeaturedServiceProviderOfAccessibleCommunity(Long serviceProviderCommunityId, PermissionFilter permissionFilter);
}
