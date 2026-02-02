package com.scnsoft.eldermark.dto.notification.event;

public class EventNotificationSecureMailDto extends EventNotificationMailDto {
    private EventDetailsNotificationDto details;

    public EventDetailsNotificationDto getDetails() {
        return details;
    }

    public void setDetails(EventDetailsNotificationDto details) {
        this.details = details;
    }
}
