package com.scnsoft.eldermark.dto.notification.event;

import com.scnsoft.eldermark.dto.notification.BaseNotificationMailDto;

public class EventNotificationMailDto extends BaseNotificationMailDto {

    private String eventGroup;
    private String eventType;
    private String communityName;
    private String responsibility;
    private String eventUrl;

    public String getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(String eventGroup) {
        this.eventGroup = eventGroup;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}
