package com.scnsoft.eldermark.converter.entity2dto.document.category;

import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentCategoryEntityToItemDtoConverter implements Converter<DocumentCategory, DocumentCategoryItemDto> {

    @Override
    public DocumentCategoryItemDto convert(DocumentCategory source) {
        var result = new DocumentCategoryItemDto();
        result.setId(source.getId());
        result.setName(source.getName());
        result.setColor(source.getColor());
        return result;
    }
}
