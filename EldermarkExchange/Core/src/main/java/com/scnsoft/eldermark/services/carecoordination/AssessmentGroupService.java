package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.AssessmentGroup;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentGroupDto;

import java.util.List;

public interface AssessmentGroupService {

    List<AssessmentGroupDto> getAllAssessmentGroups(Long patientDatabaseId);

}
