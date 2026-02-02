package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.entity.NoteStatus;
import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.entity.NoteType;
import com.scnsoft.eldermark.service.DataSourceService;
import com.scnsoft.eldermark.service.NoteService;
import com.scnsoft.eldermark.service.transformer.util.Converters;
import com.scnsoft.eldermark.web.entity.AdmitDateDto;
import com.scnsoft.eldermark.web.entity.notes.HistoryNoteItemDto;
import com.scnsoft.eldermark.web.entity.notes.NoteDetailsDto;
import com.scnsoft.eldermark.web.entity.notes.NoteEmployeeDto;
import com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoteDetailsDtoTransformer implements Converter<Note, NoteDetailsDto> {

    @Autowired
    private Converter<Note, NoteEmployeeDto> noteNoteEmployeeDtoConverter;

    @Autowired
    private NoteService noteService;

    @Autowired
    private Converter<NoteSubType, NoteSubTypeDto> noteSubTypeDtoConverter;

    @Autowired
    private Converter<Note, HistoryNoteItemDto> historyNoteItemDtoConverter;

    public static Long ADMIT_DATE_FROM_INTAKE_DATE_ID = 0l;

    @Override
    public NoteDetailsDto convert(Note note) {
        if (note == null) {
            return null;
        }
        final NoteDetailsDto noteDetailsDto = new NoteDetailsDto();

        noteDetailsDto.setId(note.getId());
        noteDetailsDto.setResidentId(note.getResident().getId());
        noteDetailsDto.setResidentName(note.getResident().getFullName());
        noteDetailsDto.setType(note.getType());
        if (noteDetailsDto.getType().equals(NoteType.EVENT_NOTE)) {
            noteDetailsDto.setEventId(note.getEvent().getId());
            noteDetailsDto.setEventType(note.getEvent().getEventType().getDescription());
            noteDetailsDto.setEventDate(note.getEvent().getEventDatetime());
            noteDetailsDto.setEventResidentName(note.getEvent().getResident().getFullName());
        }
        noteDetailsDto.setSubType(noteSubTypeDtoConverter.convert(note.getSubType()));
        noteDetailsDto.setStatus(note.getStatus());
        noteDetailsDto.setLastModifiedDate(note.getLastModifiedDate());
        noteDetailsDto.setCreator(noteNoteEmployeeDtoConverter.convert(note));
        noteDetailsDto.setDataSource(DataSourceService.transform(note.getEmployee().getDatabase(), note.getResident().getId()));
        noteDetailsDto.setSubjective(note.getSubjective());
        noteDetailsDto.setObjective(note.getObjective());
        noteDetailsDto.setAssessment(note.getAssessment());
        noteDetailsDto.setPlan(note.getPlan());
        noteDetailsDto.setIsArchived(note.getArchived());
        noteDetailsDto.setIsEditable(noteService.canEditNote(note));

        if (note.getStatus().equals(NoteStatus.UPDATED) && BooleanUtils.isFalse(note.getArchived())) {
            final List<Note> history = noteService.getHistoryNotes(note);

            if (!CollectionUtils.isEmpty(history)) {
                final List<HistoryNoteItemDto> transformedHistory = Converters.convertAll(history, historyNoteItemDtoConverter);
                noteDetailsDto.setChangeHistory(transformedHistory);
            }
        }
        AdmitDateDto admitDateDto = new AdmitDateDto();
        if (note.getAdmittanceHistory() != null) {
            admitDateDto.setId(note.getAdmittanceHistory().getId());
            admitDateDto.setValue(note.getAdmittanceHistory().getAdmitDate());
            noteDetailsDto.setAdmitDate(admitDateDto);
        } else if (note.getIntakeDate() != null) {
            admitDateDto.setId(ADMIT_DATE_FROM_INTAKE_DATE_ID);
            admitDateDto.setValue(note.getIntakeDate());
            noteDetailsDto.setAdmitDate(admitDateDto);
        }
        return noteDetailsDto;
    }
}
