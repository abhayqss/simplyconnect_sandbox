package com.scnsoft.eldermark.converter.assessment;

import com.scnsoft.eldermark.dto.assessment.AssessmentDefaultsDto;
import com.scnsoft.eldermark.entity.Client;

public interface AssessmentResultDefaultDtoAggregator<DEFAULTS_DTO extends AssessmentDefaultsDto>{
    String getAssessmentShortName();
    DEFAULTS_DTO aggregateDefaults(Client client, final Long parentAssessmentResultId);
}
