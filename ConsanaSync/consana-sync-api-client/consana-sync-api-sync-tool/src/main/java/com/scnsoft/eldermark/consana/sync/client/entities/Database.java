package com.scnsoft.eldermark.consana.sync.client.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "SourceDatabase")
public class Database extends BaseReadOnlyEntity {

    @Column(name = "last_success_sync_date")
    private Instant lastSyncSuccessDate;

    public Instant getLastSyncSuccessDate() {
        return lastSyncSuccessDate;
    }

    public void setLastSyncSuccessDate(Instant lastSyncSuccessDate) {
        this.lastSyncSuccessDate = lastSyncSuccessDate;
    }
}
