package com.scnsoft.eldermark.service;


import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.event.EventType;

import java.util.List;
import java.util.Set;

public interface EventTypeService {

    EventType findById(Long id);

    EventType findByCode(String code);

    Set<String> findDisabledCodesByRoles(List<CareTeamRoleCode> careTeamRoleCodes);

    List<Long> findDisabledIdsByRoles(List<CareTeamRoleCode> careTeamRoleCodes);
}
