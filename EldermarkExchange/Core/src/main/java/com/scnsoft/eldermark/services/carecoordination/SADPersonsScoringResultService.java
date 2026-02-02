package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.assessments.SADPersonsAssessmentScoringResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SADPersonsScoringResultService {

    Page<SADPersonsAssessmentScoringResult> getScoringGroups(Long assessmentId, Pageable pageable);

}
