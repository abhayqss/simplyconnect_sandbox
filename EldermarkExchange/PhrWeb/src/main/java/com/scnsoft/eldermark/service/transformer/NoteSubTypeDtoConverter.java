package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.shared.carecoordination.notes.NoteSubTypeDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NoteSubTypeDtoConverter implements Converter<NoteSubTypeDto, com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto> {
    @Override
    public com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto convert(NoteSubTypeDto noteSubTypeDto) {
        if (noteSubTypeDto == null) {
            return null;
        }
        return new com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto(noteSubTypeDto.getId(), noteSubTypeDto.getLabel(),
                noteSubTypeDto.getFollowUpCode());
    }
}
