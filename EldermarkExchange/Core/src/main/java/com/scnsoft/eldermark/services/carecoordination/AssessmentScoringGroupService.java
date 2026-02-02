package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentHistoryDto;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentScoringGroupListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AssessmentScoringGroupService {
    Page<AssessmentScoringGroupListDto> getScoringGroups(Long assessmentId, Pageable pageable);

    Page<AssessmentHistoryDto> getHistory(Long assessmentId, Pageable pageable);
}
