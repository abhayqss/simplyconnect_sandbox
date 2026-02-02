package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Note;
import org.springframework.core.convert.converter.Converter;

public interface NoteIdConverter extends Converter<Note, Long> {

}
