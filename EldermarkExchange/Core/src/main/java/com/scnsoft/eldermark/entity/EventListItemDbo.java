package com.scnsoft.eldermark.entity;

import java.util.Date;


/**
 * Created by pzhurba on 09-Oct-15.
 */
public class EventListItemDbo {
    private Long eventId;
    private String eventType;
    private Long eventGroupId;
    private Long residentId;
    private String residentFirstName;
    private String residentLastName;
    private Date eventDate;


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

    public Long getEventGroupId() {
        return eventGroupId;
    }

    public void setEventGroupId(Long eventGroupId) {
        this.eventGroupId = eventGroupId;
    }

    public String getResidentFirstName() {
        return residentFirstName;
    }

    public void setResidentFirstName(String residentFirstName) {
        this.residentFirstName = residentFirstName;
    }

    public String getResidentLastName() {
        return residentLastName;
    }

    public void setResidentLastName(String residentLastName) {
        this.residentLastName = residentLastName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public String toString() {
        return "EventListItemDbo{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", eventGroupId=" + eventGroupId +
                ", residentId=" + residentId +
                ", residentFirstName='" + residentFirstName + '\'' +
                ", residentLastName='" + residentLastName + '\'' +
                ", eventDate=" + eventDate +
                '}';
    }
}
