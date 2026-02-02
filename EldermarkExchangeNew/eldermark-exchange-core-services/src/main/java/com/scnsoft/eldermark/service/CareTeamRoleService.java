package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;

import java.util.List;

public interface CareTeamRoleService {

    Long ANY_TARGET_ROLE = -1L;
    Long ANOTHER_TARGET_ROLE = -2L;

    CareTeamRole get(CareTeamRoleCode code);

    List<CareTeamRole> findContactEditableRoles(CareTeamRole role);

    List<CareTeamRole> findClientCareTeamMemberEditableRoles(CareTeamRole role);

    List<CareTeamRole> findCommunityCareTeamMemberEditableRoles(CareTeamRole role);

    boolean isEditableContactRole(CareTeamRole loggedUserSystemRole, Long currentRoleId, Long roleToCheckId);

    boolean isEditableClientCareTeamMemberRole(CareTeamRole loggedUserSystemRole, Long currentRoleId, Long roleToCheckId);

    boolean isEditableCommunityCareTeamMemberRole(CareTeamRole loggedUserSystemRole, Long currentRoleId, Long roleToCheckId);

    List<CareTeamRole> findAllowedCtmRolesForEmployee(Long employeeId);

    List<CareTeamRole> findAllowedCtmRolesForEmployeeRole(CareTeamRole employeeRole);
}
