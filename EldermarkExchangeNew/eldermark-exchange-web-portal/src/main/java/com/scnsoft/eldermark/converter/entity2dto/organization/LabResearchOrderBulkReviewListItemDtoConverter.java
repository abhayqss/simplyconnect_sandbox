package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.lab.LabResearchOrderBulkReviewListItemDto;
import com.scnsoft.eldermark.dto.lab.LabResearchResultDocumentDto;
import com.scnsoft.eldermark.entity.lab.review.LabResearchOrderBulkReviewListItem;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LabResearchOrderBulkReviewListItemDtoConverter implements ListAndItemConverter<LabResearchOrderBulkReviewListItem, LabResearchOrderBulkReviewListItemDto> {
    @Override
    public LabResearchOrderBulkReviewListItemDto convert(LabResearchOrderBulkReviewListItem source) {
        var target = new LabResearchOrderBulkReviewListItemDto();
        target.setId(source.getId());
        target.setClientId(source.getClientId());
        target.setClientName(source.getClientFullName());
        target.setOrderDate(DateTimeUtils.toEpochMilli(source.getOrderDate()));
        if (CollectionUtils.isNotEmpty(source.getLabResearchOrderDocuments())) {
            target.setDocuments(source.getLabResearchOrderDocuments().stream()
                    .map(document -> new LabResearchResultDocumentDto(document.getDocumentId(), document.getDocumentTitle(),
                            DocumentUtils.resolveMimeType(document.getDocumentOriginalFileName(), document.getDocumentTitle(), document.getMimeType())))
                    .collect(Collectors.toList()));
        }
        return target;
    }
}
