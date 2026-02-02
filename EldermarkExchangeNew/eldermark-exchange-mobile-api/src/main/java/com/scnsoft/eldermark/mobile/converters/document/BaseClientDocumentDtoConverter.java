package com.scnsoft.eldermark.mobile.converters.document;

import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.mobile.dto.document.BaseDocumentDto;
import com.scnsoft.eldermark.mobile.dto.document.DocumentCategoryItemDto;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseClientDocumentDtoConverter<T extends BaseDocumentDto> implements Converter<ClientDocument, T> {

    @Autowired
    private Converter<DocumentCategory, DocumentCategoryItemDto> categoryDtoConverter;

    void fillBase(ClientDocument source, T target) {
        target.setId(source.getId());
        target.setTitle(source.getDocumentTitle());
        target.setCreatedOrModifiedDate(
                DateTimeUtils.toEpochMilli(
                        Optional.ofNullable(source.getUpdateTime())
                                .orElse(source.getCreationTime())
                ));
        target.setMimeType(DocumentUtils.resolveMimeType(source));

        var categories = source.getCategories().stream()
                .map(categoryDtoConverter::convert)
                .sorted(Comparator.comparing(DocumentCategoryItemDto::getName))
                .collect(Collectors.toList());

        target.setCategories(categories);
    }
}
