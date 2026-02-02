package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentGroupDto;

import java.util.List;

public interface AssessmentFacade {

    void sortAssessmentGroups(List<AssessmentGroupDto> assessmentGroupDtos);

}
