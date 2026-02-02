package com.scnsoft.eldermark.consana.sync.client.model.entities.logging;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateType;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ConsanaPatientDispatchLog")
public class ConsanaPatientDispatchLog extends ConsanaDispatchLog {

    @Enumerated(EnumType.STRING)
    @Column(name = "update_type")
    private ConsanaPatientUpdateType updateType;

    @Column(name = "update_time")
    private Instant updateTime;

    @Column(name = "was_already_processed_datetime")
    private Instant wasAlreadyProcessedDatetime;

    public ConsanaPatientUpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(ConsanaPatientUpdateType updateType) {
        this.updateType = updateType;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public Instant getWasAlreadyProcessedDatetime() {
        return wasAlreadyProcessedDatetime;
    }

    public void setWasAlreadyProcessedDatetime(Instant wasAlreadyProcessedDatetime) {
        this.wasAlreadyProcessedDatetime = wasAlreadyProcessedDatetime;
    }
}
