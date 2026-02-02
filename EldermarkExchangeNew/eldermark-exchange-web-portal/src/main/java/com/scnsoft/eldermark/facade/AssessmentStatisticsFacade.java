package com.scnsoft.eldermark.facade;

import java.util.List;

import com.scnsoft.eldermark.dto.StatusCountDto;

public interface AssessmentStatisticsFacade {

    Long count();

    List<StatusCountDto> countGroupedByStatus();

}
