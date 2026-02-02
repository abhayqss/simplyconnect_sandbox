package com.scnsoft.eldermark.facade;

import java.util.List;

import com.scnsoft.eldermark.dto.EventStatisticsDto;
import com.scnsoft.eldermark.dto.EventStatisticsFilterDto;

public interface ClientEventStatisticsFacade {

    List<EventStatisticsDto> findEventGroupCountByClientId(Long clientId, EventStatisticsFilterDto filter);
}
