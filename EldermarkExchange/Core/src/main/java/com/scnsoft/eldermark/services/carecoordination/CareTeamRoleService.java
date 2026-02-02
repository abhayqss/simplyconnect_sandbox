package com.scnsoft.eldermark.services.carecoordination;

import com.google.common.base.Function;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;

import java.util.List;

/**
 * Created by pzhurba on 05-Oct-15.
 */
public interface CareTeamRoleService {
    List<CareTeamRoleDto> getAllCareTeamRoles();
    List<CareTeamRoleDto> getNonAdminCareTeamRoles();

    CareTeamRoleDto get(Long id);
    CareTeamRoleDto get(CareTeamRoleCode code);
    Function<CareTeamRoleCode, CareTeamRoleDto> toDto();
}
