package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.ReleaseNoteListItemDto;
import com.scnsoft.eldermark.entity.ReleaseNote;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ReleaseNoteListItemDtoConverter implements Converter<ReleaseNote, ReleaseNoteListItemDto> {

    @Override
    public ReleaseNoteListItemDto convert(ReleaseNote source) {
        var target = new ReleaseNoteListItemDto();
        target.setId(source.getId());
        target.setCreatedDate(DateTimeUtils.toEpochMilli(source.getCreatedDate()));
        target.setDescription(source.getDescription());
        target.setFileMimeType(source.getMimeType());
        return target;
    }
}
