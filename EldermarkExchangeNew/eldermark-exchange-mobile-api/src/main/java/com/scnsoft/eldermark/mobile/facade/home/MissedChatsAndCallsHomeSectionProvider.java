package com.scnsoft.eldermark.mobile.facade.home;

import com.scnsoft.eldermark.mobile.dto.home.MissedChatsAndCallsHomeSectionDto;

import java.util.List;

public interface MissedChatsAndCallsHomeSectionProvider {

    List<MissedChatsAndCallsHomeSectionDto> loadMissedChatsAndCalls(Long currentEmployeeId, int limit);

}
