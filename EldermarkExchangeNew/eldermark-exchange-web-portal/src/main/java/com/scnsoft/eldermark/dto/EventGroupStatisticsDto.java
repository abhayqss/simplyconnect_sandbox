package com.scnsoft.eldermark.dto;

public class EventGroupStatisticsDto {
    
    private TypeDto eventGroup;

    private Long eventCount;
    
    public TypeDto getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(TypeDto eventGroup) {
        this.eventGroup = eventGroup;
    }

    public Long getEventCount() {
        return eventCount;
    }

    public void setEventCount(Long eventCount) {
        this.eventCount = eventCount;
    }
   
}
