package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ServiceTypeDao;
import com.scnsoft.eldermark.dao.specification.ServiceTypeSpecificationGenerator;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ServiceTypeServiceImpl implements ServiceTypeService {

    @Autowired
    private ServiceTypeDao serviceTypeDao;

    @Autowired
    private ServiceTypeSpecificationGenerator serviceTypeSpecificationGenerator;

    @Autowired
    private PartnerNetworkService partnerNetworkService;

    private static final List<Permission> CLIENT_REFERRAL_PERMISSIONS = List.of(
            Permission.CLIENT_REFERRAL_ADD_ALL_EXCEPT_OPTED_OUT,
            Permission.CLIENT_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION,
            Permission.CLIENT_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY,
            Permission.CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM,
            Permission.CLIENT_REFERRAL_ADD_IF_CURRENT_REGULAR_CLIENT_CTM,
            Permission.CLIENT_REFERRAL_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            Permission.CLIENT_REFERRAL_ADD_IF_SELF_RECORD
    );

    private static final List<Permission> B2B_REFERRAL_PERMISSIONS = List.of(
            Permission.B2B_REFERRAL_ADD_ALL,
            Permission.B2B_REFERRAL_ADD_IF_ASSOCIATED_ORGANIZATION,
            Permission.B2B_REFERRAL_ADD_IF_ASSOCIATED_COMMUNITY,
            Permission.B2B_REFERRAL_ADD_IF_CO_REGULAR_COMMUNITY_CTM
    );

    @Override
    public List<ServiceType> findAllowedForReferral(PermissionFilter permissionFilter) {
        return filterAllowedForReferral(serviceTypeDao.findAll(), permissionFilter);
    }

    @Override
    public List<ServiceType> findAllById(Collection<Long> ids) {
        return serviceTypeDao.findAllById(ids);
    }

    @Override
    public List<ServiceType> findAllByCategoryIdsAndDisplayNameLike(Collection<Long> serviceCategoryIds, String searchText, Boolean isAccessibleOnly, PermissionFilter permissionFilter) {
        var byServiceCategoryIds = serviceTypeSpecificationGenerator.byServiceCategoryIds(serviceCategoryIds);
        var byDisplayNameLike = serviceTypeSpecificationGenerator.byDisplayNameLike(searchText);
        var resultSpecification = byServiceCategoryIds.and(byDisplayNameLike);
        if (BooleanUtils.isTrue(isAccessibleOnly)) {
            resultSpecification = resultSpecification.and(serviceTypeSpecificationGenerator.fromAccessibleCommunities(permissionFilter));
        }
        return serviceTypeDao.findAll(resultSpecification);
    }

    @Override
    public List<ServiceType> findAllowedForReferralInUse(PermissionFilter permissionFilter, Long excludeCommunityId) {
        var searchInNetworks = partnerNetworkService.existsForCommunity(excludeCommunityId);
        var inUseServices = serviceTypeDao.findAll(serviceTypeSpecificationGenerator.fromReferralReceivingCommunities(
                excludeCommunityId,
                searchInNetworks
        ));
        return filterAllowedForReferral(inUseServices, permissionFilter);
    }

    @Override
    public List<ServiceType> findAllowedForReferralByCommunityId(PermissionFilter permissionFilter, Long communityId) {
        var communityServices = serviceTypeDao.findAll(serviceTypeSpecificationGenerator.byCommunityIdEligibleForDiscovery(communityId));
        return filterAllowedForReferral(communityServices, permissionFilter);
    }

    @Override
    public boolean isAllowedToCreateB2bReferral(ServiceType service, PermissionFilter permissionFilter) {
        return service.getIsBusinessRelated() && permissionFilter.hasAnyPermission(B2B_REFERRAL_PERMISSIONS);
    }

    @Override
    public boolean isAllowedToCreateClientReferral(ServiceType service, PermissionFilter permissionFilter) {
        return service.getIsClientRelated() && permissionFilter.hasAnyPermission(CLIENT_REFERRAL_PERMISSIONS);
    }

    private List<ServiceType> filterAllowedForReferral(List<ServiceType> services, PermissionFilter permissionFilter) {
        return services.stream()
                .filter(service -> {
                    var canCreateClientReferral = isAllowedToCreateClientReferral(service, permissionFilter);
                    var canCreateB2bReferral = isAllowedToCreateB2bReferral(service, permissionFilter);
                    return canCreateClientReferral || canCreateB2bReferral;
                })
                .collect(Collectors.toList());
    }
}
