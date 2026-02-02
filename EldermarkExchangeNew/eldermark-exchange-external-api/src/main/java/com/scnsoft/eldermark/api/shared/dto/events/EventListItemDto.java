package com.scnsoft.eldermark.api.shared.dto.events;

/**
 * Created by pzhurba on 09-Oct-15.
 */
public class EventListItemDto {
    private Long eventId;
    private String eventType;
    private String residentName;
    private Long eventDate;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }
}
