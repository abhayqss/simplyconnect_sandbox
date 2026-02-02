package com.scnsoft.eldermark.converter.entity2dto.document.category;

import com.scnsoft.eldermark.dto.document.category.DocumentCategoryDto;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentCategoryEntityToDtoConverter implements Converter<DocumentCategory, DocumentCategoryDto> {

    @Override
    public DocumentCategoryDto convert(DocumentCategory source) {
        var result = new DocumentCategoryDto();
        result.setId(source.getId());
        result.setName(source.getName());
        result.setColor(source.getColor());
        result.setOrganizationId(source.getOrganizationId());
        return result;
    }
}
