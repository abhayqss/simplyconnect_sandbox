package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.assessments.SlumsAssessmentScoringGroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SlumsAssessmentScoringGroupService {

    Page<SlumsAssessmentScoringGroupDto> getScoringGroups(Long assessmentId, Pageable pageable);

}
