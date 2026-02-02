package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.BaseAttachmentDto;
import com.scnsoft.eldermark.entity.BaseAttachment;
import org.springframework.stereotype.Component;

@Component
public class BaseAttachmentDtoConverter implements ListAndItemConverter<BaseAttachment, BaseAttachmentDto> {

    @Override
    public BaseAttachmentDto convert(BaseAttachment source) {
        var target = new BaseAttachmentDto();
        target.setId(source.getId());
        target.setName(source.getOriginalFileName());
        target.setMimeType(source.getMimeType());
        return target;
    }
}
