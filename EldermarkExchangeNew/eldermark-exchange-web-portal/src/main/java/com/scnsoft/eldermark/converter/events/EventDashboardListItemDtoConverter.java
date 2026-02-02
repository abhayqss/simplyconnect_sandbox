package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;
import com.scnsoft.eldermark.dto.events.EventDashboardListItemDto;
import com.scnsoft.eldermark.entity.event.EventDashboardItem;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.stereotype.Component;

@Component
public class EventDashboardListItemDtoConverter implements ListAndItemConverter<EventDashboardItem, EventDashboardListItemDto> {

    @Override
    public EventDashboardListItemDto convert(EventDashboardItem source) {
        var target = new EventDashboardListItemDto();
        target.setId(source.getId());
        target.setType(source.getEventTypeDescription());
        target.setDate(source.getEventDateTime().toEpochMilli());
        target.setAuthor(CareCoordinationUtils.getFullName(source.getEventAuthorFirstName(), source.getEventAuthorLastName()));
        target.setGroup(new NamedTitledEntityDto(source.getEventTypeEventGroupCode(), source.getEventTypeEventGroupName()));
        return target;
    }

}
