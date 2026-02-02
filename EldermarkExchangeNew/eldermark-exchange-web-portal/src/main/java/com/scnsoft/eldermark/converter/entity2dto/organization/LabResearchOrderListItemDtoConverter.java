package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.lab.LabResearchOrderListItemDto;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderListItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LabResearchOrderListItemDtoConverter implements Converter<LabResearchOrderListItem, LabResearchOrderListItemDto> {
    @Override
    public LabResearchOrderListItemDto convert(LabResearchOrderListItem source) {
        var target = new LabResearchOrderListItemDto();
        target.setId(source.getId());
        target.setCreatedByName(source.getCreatedPersonFullName());
        target.setClientId(source.getClientId());
        target.setClientName(source.getClientFullName());
        target.setStatusName(source.getStatus().name());
        target.setStatusTitle(source.getStatus().getDisplayName());
        target.setReason(source.getReason().getValue());
        target.setRequisitionNumber(source.getRequisitionNumber());
        target.setCommunity(source.getClientCommunityName());
        target.setCreatedDate(source.getCreatedDate().toEpochMilli());
        target.setAvatarId(source.getClientAvatarId());
        return target;
    }
}
