package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteResidentAdmittanceHistoryDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.RelatedNoteItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoteService {

    Page<NoteListItemDto> listPatientNotes(Long patientId, Pageable pageRequest);

    List<RelatedNoteItemDto> getRelatedEventNotes(Long eventId);

    Long count(Long patientId);

    Integer getPageNumber(Long noteId, Long patientId);

    Long createPatientNote(NoteDto noteDto);

    Long createEventNote(NoteDto noteDto);

    Long editNote(NoteDto noteDto);

    void checkAddedBySelfOrThrow(Long noteId);

    boolean isAddedBySelf(Long noteId);

    Long getLatestForNote(Long noteId);

    List<NoteResidentAdmittanceHistoryDto> getNoteAdmittanceHistoryForResidentWithIntakeDate(Long residentId);

    List<NoteResidentAdmittanceHistoryDto> getNoteAdmittanceHistoryForEventWithIntakeDate(Long eventId);

    List<Long> getTakenAdmitIntakeHistoryIdForSubTypeForEvent(Long eventId, NoteSubType.FollowUpCode followUpCode);
}
