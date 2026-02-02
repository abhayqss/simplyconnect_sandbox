package com.scnsoft.eldermark.mobile.facade.home;

import com.scnsoft.eldermark.mobile.dto.home.CareTeamUpdateHomeSectionDto;

import java.util.List;
import java.util.Set;

public interface CareTeamUpdateHomeSectionProvider {

    List<CareTeamUpdateHomeSectionDto> loadCareTeamUpdates(Long currentEmployeeId, Set<Long> clientIds, int limit);
}
