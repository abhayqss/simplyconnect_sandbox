package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;

import java.util.Collection;
import java.util.List;

public interface ServiceTypeService {

    List<ServiceType> findAllowedForReferral(PermissionFilter permissionFilter);

    List<ServiceType> findAllById(Collection<Long> ids);

    List<ServiceType> findAllByCategoryIdsAndDisplayNameLike(Collection<Long> serviceCategoryIds, String searchText, Boolean isAccessibleOnly, PermissionFilter permissionFilter);

    List<ServiceType> findAllowedForReferralInUse(PermissionFilter permissionFilter, Long excludeCommunityId);

    List<ServiceType> findAllowedForReferralByCommunityId(PermissionFilter permissionFilter, Long communityId);

    boolean isAllowedToCreateB2bReferral(ServiceType service, PermissionFilter permissionFilter);

    boolean isAllowedToCreateClientReferral(ServiceType service, PermissionFilter permissionFilter);
}
