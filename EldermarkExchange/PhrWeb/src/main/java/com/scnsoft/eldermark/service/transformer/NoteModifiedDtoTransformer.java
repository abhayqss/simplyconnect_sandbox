package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.web.entity.notes.NoteModifiedDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NoteModifiedDtoTransformer implements Converter<Note, NoteModifiedDto> {

    @Override
    public NoteModifiedDto convert(Note note) {
        if (note == null) {
            return null;
        }
        final NoteModifiedDto noteModifiedDto = new NoteModifiedDto();
        noteModifiedDto.setId(note.getId());
        noteModifiedDto.setType(note.getType());
        noteModifiedDto.setStatus(note.getStatus());
        return noteModifiedDto;
    }
}
