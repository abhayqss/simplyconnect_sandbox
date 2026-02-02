package com.scnsoft.eldermark.facade;

import java.util.List;

import com.scnsoft.eldermark.dto.StatusCountDto;

public interface ServicePlanStatisticsFacade {

    Long count();

    List<StatusCountDto> countGroupedByStatus();
}
