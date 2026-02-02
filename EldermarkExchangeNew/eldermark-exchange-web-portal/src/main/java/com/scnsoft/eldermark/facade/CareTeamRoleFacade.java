package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.RoleDto;

import java.util.List;

public interface CareTeamRoleFacade {

    List<RoleDto> findEditableSystemRoles();

    List<RoleDto> findEditableClientCareTeamMemberRoles(Long employeeId);

    List<RoleDto> findEditableCommunityCareTeamMemberRoles();
}
