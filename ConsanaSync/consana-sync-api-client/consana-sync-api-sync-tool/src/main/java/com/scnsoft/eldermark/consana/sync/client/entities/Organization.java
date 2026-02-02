package com.scnsoft.eldermark.consana.sync.client.entities;

import javax.persistence.*;

@Entity
@Table(name = "Organization")
public class Organization extends BaseReadOnlyEntity {

    @Column(name = "is_consana_initial_sync")
    private Boolean isConsanaInitialSync;

    @ManyToOne
    @JoinColumn(name = "database_id")
    private Database database;

    public Boolean getConsanaInitialSync() {
        return isConsanaInitialSync;
    }

    public void setConsanaInitialSync(Boolean consanaInitialSync) {
        isConsanaInitialSync = consanaInitialSync;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
}
