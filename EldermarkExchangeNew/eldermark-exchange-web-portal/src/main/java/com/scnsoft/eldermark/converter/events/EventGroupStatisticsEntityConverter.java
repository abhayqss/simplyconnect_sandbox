package com.scnsoft.eldermark.converter.events;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.beans.EventGroupStatistics;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.EventGroupStatisticsDto;
import com.scnsoft.eldermark.dto.TypeDto;

@Component
public class EventGroupStatisticsEntityConverter
        implements ListAndItemConverter<EventGroupStatistics, EventGroupStatisticsDto> {

    @Override
    public EventGroupStatisticsDto convert(EventGroupStatistics source) {
        EventGroupStatisticsDto target = new EventGroupStatisticsDto();
        target.setEventCount(source.getCount());
        TypeDto type = new TypeDto();
        type.setName(source.getGroupName());
        type.setTitle(source.getGroupName());
        target.setEventGroup(type);
        return target;
    }

}
