package com.scnsoft.eldermark.converter.dto2entity.document.category;

import com.scnsoft.eldermark.dto.document.category.DocumentCategoryDto;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentCategoryDtoToEntityConverter implements Converter<DocumentCategoryDto, DocumentCategory> {

    @Override
    public DocumentCategory convert(DocumentCategoryDto source) {
        var target = new DocumentCategory();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setColor(source.getColor());
        target.setOrganizationId(source.getOrganizationId());
        return target;
    }
}
