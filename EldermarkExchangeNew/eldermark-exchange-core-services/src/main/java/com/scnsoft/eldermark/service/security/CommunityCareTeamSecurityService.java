package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.CareTeamSecurityFieldsAware;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;

public interface CommunityCareTeamSecurityService {

    boolean canView(CommunityCareTeamMember communityCareTeamMember);

    boolean canViewList();

    boolean canEdit(CommunityCareTeamMember communityCareTeamMember, Long targetCareTeamRoleId);

    boolean canAdd(CareTeamSecurityFieldsAware dto);

    boolean canDelete(CommunityCareTeamMember careTeamMember);

    boolean canDelete(CommunityCareTeamMember careTeamMember, PermissionFilter permissionFilter);

    boolean canAddAnyRoleAndEmployee(Long communityId, AffiliatedCareTeamType type);

}
