package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NoteSubTypeDtoTransformer implements Converter<NoteSubType, NoteSubTypeDto> {
    @Override
    public NoteSubTypeDto convert(NoteSubType noteSubType) {
        if (noteSubType == null) {
            return null;
        }
        return new NoteSubTypeDto(noteSubType.getId(), noteSubType.getDescription(), noteSubType.getFollowUpCode());
    }
}
