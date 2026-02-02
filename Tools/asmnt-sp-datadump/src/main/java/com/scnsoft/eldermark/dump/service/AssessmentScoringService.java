package com.scnsoft.eldermark.dump.service;

import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult;

public interface AssessmentScoringService {
    Long calculateScore(Long assessmentId, String resultJson);
    Long calculateScore(ClientAssessmentResult clientAssessmentResult);
}
