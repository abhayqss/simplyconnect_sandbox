package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.ResidentAssessmentResult;
import com.scnsoft.eldermark.shared.carecoordination.assessments.ResidentAssessmentScoringDto;

import java.util.Date;

public interface ResidentAssessmentResultNotificationService {

    Event sendAssessmentNotifications(ResidentAssessmentResult residentAssessmentResult, Long score);
    void createNoteForResidentAssessmentResultEvent(Date lastModifiedDate, ResidentAssessmentResult residentAssessmentResult, ResidentAssessmentScoringDto dto);
}
