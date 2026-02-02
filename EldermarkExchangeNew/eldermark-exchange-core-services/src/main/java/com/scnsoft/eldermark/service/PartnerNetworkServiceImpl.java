package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.PartnerNetworkDetailsFilter;
import com.scnsoft.eldermark.beans.PartnerNetworkFilter;
import com.scnsoft.eldermark.beans.PartnerNetworkOrganization;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.PartnerNetworkCommunitySecurityAwareEntity;
import com.scnsoft.eldermark.dao.PartnerNetworkCommunityDao;
import com.scnsoft.eldermark.dao.PartnerNetworkDao;
import com.scnsoft.eldermark.dao.specification.PartnerNetworkSpecificationGenerator;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PartnerNetworkServiceImpl implements PartnerNetworkService {

    @Autowired
    private PartnerNetworkDao partnerNetworkDao;

    @Autowired
    private PartnerNetworkCommunityDao partnerNetworkCommunityDao;

    @Autowired
    private PartnerNetworkSpecificationGenerator partnerNetworkSpecificationGenerator;

    @Override
    public List<PartnerNetwork> find(PartnerNetworkFilter filter, PermissionFilter permissionFilter) {
        var byFilter = partnerNetworkSpecificationGenerator.byFilter(filter);
        var hasAccess = partnerNetworkSpecificationGenerator.hasAccess(permissionFilter);

        return partnerNetworkDao.findAll(byFilter.and(hasAccess));
    }

    @Override
    public List<PartnerNetworkCommunity> findByPartnerNetworkId(Long partnerNetworkId) {
        var byPartnerNetworkId = partnerNetworkSpecificationGenerator.byPartnerNetworkId(partnerNetworkId);
        var eligibleForDiscovery = partnerNetworkSpecificationGenerator.communityEligibleForDiscovery();

        return partnerNetworkCommunityDao.findAll(byPartnerNetworkId.and(eligibleForDiscovery));
    }

    @Override
    public List<PartnerNetworkCommunitySecurityAwareEntity> findSecurityByPartnerNetworkId(Long partnerNetworkId) {
        var byPartnerNetworkId = partnerNetworkSpecificationGenerator.byPartnerNetworkId(partnerNetworkId);
        var eligibleForDiscovery = partnerNetworkSpecificationGenerator.communityEligibleForDiscovery();

        return partnerNetworkCommunityDao.findAll(byPartnerNetworkId.and(eligibleForDiscovery),
                PartnerNetworkCommunitySecurityAwareEntity.class);
    }

    @Override
    public Page<PartnerNetworkOrganization> findGroupedByOrganization(PartnerNetworkDetailsFilter filter, Pageable organizationPageable) {
        var byDetailsFilterAndEligibleForDiscovery = partnerNetworkSpecificationGenerator.byDetailsFilterAndEligibleForDiscovery(filter);

        return partnerNetworkDao.findGroupedByOrganization(byDetailsFilterAndEligibleForDiscovery, organizationPageable);
    }

    @Override
    public boolean existsForCommunity(Long communityId) {
        var byCommunityId = partnerNetworkSpecificationGenerator.byCommunityId(communityId);
        var eligibleForDiscovery = partnerNetworkSpecificationGenerator.communityEligibleForDiscovery();

        return partnerNetworkCommunityDao.exists(byCommunityId.and(eligibleForDiscovery));
    }

    @Override
    public boolean isCommunityInNetwork(Long communityId, Long partnerNetworkId) {
        var byCommunityId = partnerNetworkSpecificationGenerator.byCommunityId(communityId);
        var byPartnerNetworkId = partnerNetworkSpecificationGenerator.byPartnerNetworkId(partnerNetworkId);
        var eligibleForDiscovery = partnerNetworkSpecificationGenerator.communityEligibleForDiscovery();

        return partnerNetworkCommunityDao.exists(byCommunityId.and(byPartnerNetworkId).and(eligibleForDiscovery));
    }

    @Override
    public boolean areInSameNetwork(Long... communityIds) {
        var byAllCommunities = partnerNetworkSpecificationGenerator.withSameCommunitiesEiligibleForDiscovery(Arrays.asList(communityIds));
        return partnerNetworkDao.exists(byAllCommunities);
    }

    @Override
    public List<PartnerNetwork> findNetworksWithAllCommunities(Long... communityIds) {
        var byAllCommunities = partnerNetworkSpecificationGenerator.withSameCommunitiesEiligibleForDiscovery(Arrays.asList(communityIds));
        return partnerNetworkDao.findAll(byAllCommunities);
    }
}
