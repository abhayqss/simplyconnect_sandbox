package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.FeaturedServiceProviderDao;
import com.scnsoft.eldermark.dao.specification.FeaturedServiceProviderSpecificationGenerator;
import com.scnsoft.eldermark.entity.FeaturedServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class FeaturedServiceProviderServiceImpl implements FeaturedServiceProviderService {

    @Autowired
    private FeaturedServiceProviderDao featuredServiceProviderDao;

    @Autowired
    private FeaturedServiceProviderSpecificationGenerator featuredServiceProviderSpecificationGenerator;

    @Override
    public void saveAll(List<FeaturedServiceProvider> list) {
        featuredServiceProviderDao.saveAll(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturedServiceProvider> findAllByCommunityIdAndProviderIdIn(Long communityId, Collection<Long> providerIds) {
        return featuredServiceProviderDao.findAllByCommunityIdAndProviderIdIn(communityId, providerIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturedServiceProvider> fetchServiceProvidersByCommunityId(Long communityId) {
        return featuredServiceProviderDao.findAllByCommunityId(communityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturedServiceProvider> findAllDiscoverableByCommunityId(Long communityId) {
        return featuredServiceProviderDao.findAllByCommunityIdAndProvider_Marketplace_DiscoverableIsTrue(communityId);
    }

    @Override
    public void deleteByCommunityIdAndProviderIdIn(Long communityId, List<Long> providerIds) {
        featuredServiceProviderDao.deleteByCommunityIdAndProviderIdIn(communityId, providerIds);
    }

    @Override
    public boolean isFeaturedServiceProviderOfAccessibleCommunity(Long serviceProviderCommunityId, PermissionFilter permissionFilter) {

        var byProviderId = featuredServiceProviderSpecificationGenerator.byServiceProviderCommunityId(serviceProviderCommunityId);
        var byAccessibleCommunity = featuredServiceProviderSpecificationGenerator.byAccessibleCommunity(permissionFilter);

        return featuredServiceProviderDao.exists(byProviderId.and(byAccessibleCommunity));
    }
}
