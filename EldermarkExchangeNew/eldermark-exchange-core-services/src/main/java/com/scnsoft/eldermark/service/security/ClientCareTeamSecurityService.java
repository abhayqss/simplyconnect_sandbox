package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.CareTeamSecurityFieldsAware;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;

public interface ClientCareTeamSecurityService {

    boolean canView(ClientCareTeamMember clientCareTeamMember);

    boolean canViewList(Long clientId);

    boolean canEdit(ClientCareTeamMember clientCareTeamMember, Long targetCareTeamRoleId);

    boolean canAdd(CareTeamSecurityFieldsAware dto);

    boolean canDelete(ClientCareTeamMember careTeamMember);

    boolean canDelete(ClientCareTeamMember careTeamMember, PermissionFilter permissionFilter);

    boolean canAddAnyRoleAndEmployee(Long clientId, AffiliatedCareTeamType type);

}
