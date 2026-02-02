package com.scnsoft.eldermark.consana.sync.client.model.queue;

import java.util.Objects;

public class EventCreatedQueueDto {

    private Long eventId;

    public EventCreatedQueueDto() {
    }

    public EventCreatedQueueDto(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "EventCreatedDto{" +
                "eventId=" + eventId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventCreatedQueueDto that = (EventCreatedQueueDto) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
