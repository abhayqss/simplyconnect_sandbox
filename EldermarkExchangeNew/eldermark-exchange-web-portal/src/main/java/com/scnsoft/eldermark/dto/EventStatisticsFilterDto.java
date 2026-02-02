package com.scnsoft.eldermark.dto;

import java.util.List;

public class EventStatisticsFilterDto {

    private List<EventStatisticsDateDto> eventStatisticsFilterDates;

    public List<EventStatisticsDateDto> getEventStatisticsFilterDates() {
        return eventStatisticsFilterDates;
    }

    public void setEventStatisticsFilterDates(List<EventStatisticsDateDto> eventStatisticsFilterDates) {
        this.eventStatisticsFilterDates = eventStatisticsFilterDates;
    }
}
