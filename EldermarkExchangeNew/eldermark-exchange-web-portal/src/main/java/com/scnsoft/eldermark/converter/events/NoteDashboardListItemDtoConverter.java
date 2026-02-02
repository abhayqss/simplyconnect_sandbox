package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;
import com.scnsoft.eldermark.dto.notes.NoteDashboardListItemDto;
import com.scnsoft.eldermark.entity.note.NoteDashboardItem;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class NoteDashboardListItemDtoConverter implements ListAndItemConverter<NoteDashboardItem, NoteDashboardListItemDto> {

    @Override
    public NoteDashboardListItemDto convert(NoteDashboardItem source) {
        var target = new NoteDashboardListItemDto();
        target.setId(source.getId());
        if (StringUtils.isNotEmpty(source.getSubjective())) {
            target.setText(source.getSubjective());
        } else if (StringUtils.isNotEmpty(source.getObjective())) {
            target.setText(source.getObjective());
        } else if (StringUtils.isNotEmpty(source.getAssessment())) {
            target.setText(source.getAssessment());
        } else if (StringUtils.isNotEmpty(source.getPlan())) {
            target.setText(source.getPlan());
        }
        target.setType(new NamedTitledEntityDto(source.getType().name(), source.getType().getDisplayName()));
        target.setSubType(new NamedTitledEntityDto(source.getSubTypeCode(), source.getSubTypeDescription()));
        target.setDate(DateTimeUtils.toEpochMilli(source.getLastModifiedDate()));
        return target;
    }
}
