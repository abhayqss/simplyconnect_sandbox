package com.scnsoft.eldermark.dto;

import java.util.List;

public class EventStatisticsDto extends  EventStatisticsDateDto {
    
    private List<EventGroupStatisticsDto> eventGroupStatisticsList;

    public List<EventGroupStatisticsDto> getEventGroupStatisticsList() {
        return eventGroupStatisticsList;
    }

    public void setEventGroupStatisticsList(List<EventGroupStatisticsDto> eventGroupStatisticsList) {
        this.eventGroupStatisticsList = eventGroupStatisticsList;
    }
}
