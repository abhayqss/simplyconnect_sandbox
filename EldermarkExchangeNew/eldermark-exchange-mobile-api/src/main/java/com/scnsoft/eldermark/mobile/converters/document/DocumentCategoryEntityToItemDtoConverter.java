package com.scnsoft.eldermark.mobile.converters.document;

import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.mobile.dto.document.DocumentCategoryItemDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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