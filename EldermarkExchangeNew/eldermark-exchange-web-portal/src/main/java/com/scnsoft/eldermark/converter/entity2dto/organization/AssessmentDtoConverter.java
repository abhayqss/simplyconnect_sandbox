package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.assessment.AssessmentTypeDto;
import com.scnsoft.eldermark.entity.assessment.Assessment;

@Component
public class AssessmentDtoConverter implements ListAndItemConverter<Assessment, AssessmentTypeDto> {

	@Override
	public AssessmentTypeDto convert(Assessment assessment) {
        AssessmentTypeDto result = new AssessmentTypeDto();
        result.setId(assessment.getId());
        result.setTitle(assessment.getName());
        result.setShortTitle(assessment.getShortName());
        result.setName(assessment.getCode());
        result.setResultTitle(assessment.getSeverityColumnName());
        return result;
	}


}
