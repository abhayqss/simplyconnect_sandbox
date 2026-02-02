package com.scnsoft.eldermark.converter.entity2dto.signature;

import com.scnsoft.eldermark.dto.signature.DocumentSignatureTemplateListItemDto;
import com.scnsoft.eldermark.projection.signature.DocumentSignatureTemplateListItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DocumentSignatureTemplateListItemDtoConverter implements Converter<DocumentSignatureTemplateListItem, DocumentSignatureTemplateListItemDto> {

    @Override
    public DocumentSignatureTemplateListItemDto convert(DocumentSignatureTemplateListItem source) {
        var dto = new DocumentSignatureTemplateListItemDto();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setTitle(source.getTitle());
        dto.setIsFillable(StringUtils.isNotEmpty(source.getFormSchema()));
        dto.setStatusName(source.getStatus());
        if (source.getStatus() != null){
            dto.setStatusTitle(source.getStatus().getDisplayName());
        }
        return dto;
    }
}
