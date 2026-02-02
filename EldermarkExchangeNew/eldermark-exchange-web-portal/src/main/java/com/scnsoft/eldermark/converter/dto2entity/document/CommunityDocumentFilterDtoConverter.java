package com.scnsoft.eldermark.converter.dto2entity.document;

import com.scnsoft.eldermark.beans.CommunityDocumentFilter;
import com.scnsoft.eldermark.dto.document.CommunityDocumentFilterDto;
import com.scnsoft.eldermark.service.document.category.DocumentCategoryService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommunityDocumentFilterDtoConverter implements Converter<CommunityDocumentFilterDto, CommunityDocumentFilter> {

    @Autowired
    private DocumentCategoryService documentCategoryService;

    @Override
    public CommunityDocumentFilter convert(CommunityDocumentFilterDto source) {

        var target = new CommunityDocumentFilter();
        target.setTitle(source.getTitle());
        target.setCommunityId(source.getCommunityId());
        target.setFolderId(source.getFolderId());
        target.setDescription(source.getDescription());
        target.setFromDate(DateTimeUtils.toInstant(source.getFromDate()));
        target.setToDate(DateTimeUtils.toInstant(source.getToDate()));
        target.setIncludeDeleted(source.getIncludeDeleted());
        target.setIncludeNotCategorized(source.getIncludeNotCategorized());
        if (CollectionUtils.isNotEmpty(source.getCategoryIds())) {
            target.setCategoryChainIds(documentCategoryService.findChainCategoryIdsByIds(source.getCategoryIds()));
        }

        return target;
    }
}
