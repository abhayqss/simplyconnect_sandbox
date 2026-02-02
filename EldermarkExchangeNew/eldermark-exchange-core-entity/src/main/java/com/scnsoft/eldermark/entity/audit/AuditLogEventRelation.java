package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.event.Event;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Event")
public class AuditLogEventRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "event_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Event event;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(eventId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.EVENT;
    }
}
