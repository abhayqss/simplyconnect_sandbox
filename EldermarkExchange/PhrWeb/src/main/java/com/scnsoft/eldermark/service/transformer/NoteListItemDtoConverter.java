package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.web.entity.notes.NoteListItemDto;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public interface NoteListItemDtoConverter extends Converter<Note, NoteListItemDto> {
    NoteListItemDtoConverter addToReadMap(Map<Long, Boolean> readMap);
}
