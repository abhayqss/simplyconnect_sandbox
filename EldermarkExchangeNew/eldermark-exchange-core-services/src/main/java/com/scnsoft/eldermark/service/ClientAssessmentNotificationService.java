package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.Map;

public interface ClientAssessmentNotificationService {
    Event createAssessmentEvent(ClientAssessmentResult clientAssessmentResult, Pair<Long, Map<String,String>> scoreWithNegativeAnswers);
    Long createAssessmentEventNote(ClientAssessmentResult previousAssessmentResult, ClientAssessmentResult updatedAssessmentResult, Long updatedScore, String updatedSeverity);
}
