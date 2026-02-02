package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.ContactSecurityFieldsAware;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;

import java.util.Collection;

public interface ContactSecurityService {

    Long ANY_TARGET_COMMUNITY = -1L;

    boolean canAdd(ContactSecurityFieldsAware dto);

    boolean canAddAnyRole(Long organizationId, Long communityId);

    boolean canAddAssociatedClientContact(Long organizationId, Long communityId);

    boolean canEdit(Long employeeId, Long targetSystemRoleId);

    boolean canViewList();

    boolean canView(Long employeeId);

    boolean canViewDirectoryList(Long organizationId);

    boolean canViewDirectoryList(Collection<Long> organizationIds);

    boolean canEditRole(CareTeamRole targetContactRole);
}
