package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.web.entity.notes.HistoryNoteItemDto;
import com.scnsoft.eldermark.web.entity.notes.NoteEmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class HistoryNoteItemDtoTransformer implements Converter<Note, HistoryNoteItemDto> {

    @Autowired
    private Converter<Note, NoteEmployeeDto> noteNoteEmployeeDtoConverter;

    @Override
    public HistoryNoteItemDto convert(Note note) {
        if (note == null) {
            return null;
        }
        final HistoryNoteItemDto dto = new HistoryNoteItemDto();
        dto.setId(note.getId());
        dto.setStatus(note.getStatus());
        dto.setLastModifiedDate(note.getLastModifiedDate());
        dto.setCreator(noteNoteEmployeeDtoConverter.convert(note));
        return dto;
    }
}
