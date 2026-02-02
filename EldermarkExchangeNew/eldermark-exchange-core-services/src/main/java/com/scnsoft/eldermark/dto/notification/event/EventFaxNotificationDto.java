package com.scnsoft.eldermark.dto.notification.event;

import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;

public class EventFaxNotificationDto extends BaseFaxNotificationDto {

    private EventDetailsNotificationDto details;

    public EventDetailsNotificationDto getDetails() {
        return details;
    }

    public void setDetails(EventDetailsNotificationDto details) {
        this.details = details;
    }
}
