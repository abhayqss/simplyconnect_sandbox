package com.scnsoft.eldermark.consana.sync.client.model.queue;

import java.util.Objects;

public class ConsanaEventCreatedQueueDto {

    private Long eventId;
    private Long residentId;

    public ConsanaEventCreatedQueueDto() {
    }

    public ConsanaEventCreatedQueueDto(Long eventId, Long residentId) {
        this.eventId = eventId;
        this.residentId = residentId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    @Override
    public String toString() {
        return "ConsanaEventCreatedQueueDto{" +
                "eventId=" + eventId +
                ", residentId=" + residentId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsanaEventCreatedQueueDto)) return false;
        ConsanaEventCreatedQueueDto that = (ConsanaEventCreatedQueueDto) o;
        return Objects.equals(getEventId(), that.getEventId()) &&
                Objects.equals(getResidentId(), that.getResidentId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventId(), getResidentId());
    }
}
