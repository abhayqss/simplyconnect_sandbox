package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.dao.carecoordination.Responsibility;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.EventType;
import com.scnsoft.eldermark.entity.EventTypeCareTeamRoleXref;
import com.scnsoft.eldermark.entity.phr.Activity;

import javax.persistence.*;

/**
 * @author phomal
 * Created on 5/10/2017.
 */
@Entity
@DiscriminatorValue("EVENT")
public class EventActivity extends Activity {

    /**
     * Event Type ID (Foreign Key to EventType)
     * @see EventType
     */
    @Column(name = "event_type_id")
    private Long eventTypeId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", insertable = false, updatable = false)
    private EventType eventType;

    /**
     * Event ID (Foreign Key to Event)
     * @see Event
     */
    @Column(name = "event_id")
    private Long eventId;

    /**
     * Responsibility code. CTM responsibility as it was at the moment of event creation.
     * @see EventTypeCareTeamRoleXref
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "responsibility", length = 2)
    private Responsibility responsibility;

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Responsibility getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(Responsibility responsibility) {
        this.responsibility = responsibility;
    }
}
