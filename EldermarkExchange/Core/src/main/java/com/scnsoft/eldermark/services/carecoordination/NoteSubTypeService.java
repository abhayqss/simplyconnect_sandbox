package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteSubTypeDto;

import java.util.List;

public interface NoteSubTypeService {

    List<NoteSubTypeDto> getAllSubTypes();

    NoteSubType getByFollowUpCode(NoteSubType.FollowUpCode followUpCode);

    NoteSubType getOtherSubType();

    NoteSubType getAssessmentSubType();

    NoteSubType getById(Long subTypeId);

    List<NoteSubType.FollowUpCode> getTakenFollowUpForAdmitDate(Long residentId, Long admittanceHistoryId);

    List<NoteSubType.FollowUpCode> getTakenFollowUpForAdmitDateForEvent(Long eventId, Long admittanceHistoryId);

    List<Long> getTakenAdmitIntakeHistoryIdForSubType(Long residentId, NoteSubType.FollowUpCode followUpCode);
}
