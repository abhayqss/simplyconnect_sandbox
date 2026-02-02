package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Assessment;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentDto;

import java.util.List;

public interface AssessmentService {

    AssessmentDto getAssessmentDetails(Long assessmentId);

}
