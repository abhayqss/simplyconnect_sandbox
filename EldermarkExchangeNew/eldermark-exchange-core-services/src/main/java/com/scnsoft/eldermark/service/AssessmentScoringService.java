package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.AssessmentScoringCalculable;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.List;
import java.util.Map;

public interface AssessmentScoringService {
    Long calculateScore(Long assessmentId, String resultJson);

    Long calculateScore(Long assessmentId, Map<String, Object> resultJson);

    <A extends AssessmentScoringCalculable> List<Pair<A, Long>> calculateScores(List<A> assessmentResults);

    Long calculateScore(ClientAssessmentResult clientAssessmentResult);

    Boolean isRiskIdentified(Long assessmentId, Long score);

    String findSeverityOfScore(Long assessmentId, Long score);

    Pair<Long, Map<String,String>> calculateScoreWithPositiveScoringAnswers(ClientAssessmentResult clientAssessmentResult);
}
