package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.CareTeamSecurityFieldsAware;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;

public interface CareTeamSecurityService {

    Long ANY_TARGET_EMPLOYEE = -1L;

    boolean canView(Long careTeamMemberId);

    boolean canViewList(Long clientId);

    boolean canEdit(Long careTeamMemberId, Long targetCareTeamRoleId);

    boolean canAdd(CareTeamSecurityFieldsAware dto);

    boolean canDelete(Long careTeamMemberId);

    boolean canDelete(CareTeamMember careTeamMember, PermissionFilter permissionFilter);

    boolean canListContactsForCareTeamInOrganization(Long organizationId, Long clientId, Long communityId);

    boolean canAddAnyRole(Long clientId, Long communityId, AffiliatedCareTeamType type);
}
