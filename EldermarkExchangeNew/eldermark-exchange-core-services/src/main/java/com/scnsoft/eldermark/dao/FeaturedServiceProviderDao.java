package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FeaturedServiceProviderDao extends AppJpaRepository<FeaturedServiceProvider, Long> {

    List<FeaturedServiceProvider> findAllByCommunityId(Long communityId);

    List<FeaturedServiceProvider> findAllByCommunityIdAndProvider_Marketplace_DiscoverableIsTrue(Long communityId);

    void deleteByCommunityIdAndProviderIdIn(Long communityId, Collection<Long> providerIds);

    List<FeaturedServiceProvider> findAllByCommunityIdAndProviderIdIn(Long communityId, Collection<Long> providerIds);
}
