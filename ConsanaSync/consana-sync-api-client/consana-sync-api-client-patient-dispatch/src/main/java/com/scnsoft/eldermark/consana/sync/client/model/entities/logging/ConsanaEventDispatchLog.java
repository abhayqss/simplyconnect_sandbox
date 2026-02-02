package com.scnsoft.eldermark.consana.sync.client.model.entities.logging;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ConsanaEventDispatchLog")
public class ConsanaEventDispatchLog extends ConsanaDispatchLog {

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "resident_id")
    private Long residentId;

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
}
