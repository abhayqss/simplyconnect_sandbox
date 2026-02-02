package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.shared.carecoordination.notes.RelatedNoteItemDto;

import java.util.ArrayList;
import java.util.List;

public class NoteUtil {

    private NoteUtil(){}

    static List<RelatedNoteItemDto> toRelatedNoteDtoList(List<Note> notes) {
        List<RelatedNoteItemDto> relatedDtoList = new ArrayList<RelatedNoteItemDto>();
        for (Note relatedNote : notes) {
            RelatedNoteItemDto relatedNoteItemDto = new RelatedNoteItemDto();
            relatedNoteItemDto.setId(relatedNote.getId());
            relatedNoteItemDto.setPatientId(relatedNote.getResident().getId());
            relatedNoteItemDto.setStatus(relatedNote.getStatus().getDisplayName());
            relatedNoteItemDto.setLastModifiedDate(relatedNote.getLastModifiedDate());
            relatedNoteItemDto.setPersonSubmittingNote(relatedNote.getEmployee().getFullName());
            relatedNoteItemDto.setRole(relatedNote.getEmployee().getCareTeamRole().getDisplayName());
            relatedDtoList.add(relatedNoteItemDto);
        }
        return relatedDtoList;
    }
}
