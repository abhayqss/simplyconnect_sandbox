package com.scnsoft.eldermark.service.transformer.impl;

import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.service.transformer.NoteIdConverter;
import org.springframework.stereotype.Component;

@Component
public class NoteIdConverterImpl implements NoteIdConverter {

    @Override
    public Long convert(Note note) {
        return note == null ? null : note.getId();
    }
}
