package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Event")
public class Event extends BaseReadOnlyEntity {

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Resident resident;

    @JoinColumn(name = "event_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private EventType eventType;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + getId() +
                ", resident=" + resident +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId()) &&
                Objects.equals(resident, event.resident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), resident);
    }
}
