package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.shared.dto.CareTeamRoleDto;
import com.scnsoft.eldermark.api.shared.dto.StateDto;
import com.scnsoft.eldermark.api.shared.entity.VitalSignType;

import java.util.List;
import java.util.Map;

public interface ExtApiInfoService {

    List<StateDto> getAllStates();

    List<CareTeamRoleDto> getAllCareTeamRoles();

    Map<VitalSignType, String> getVitalSignTypes();
}
