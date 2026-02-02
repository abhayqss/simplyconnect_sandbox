package com.scnsoft.eldermark.entity.careteam;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "CareHistory", uniqueConstraints = @UniqueConstraint(columnNames = { "legacy_id", "database_id" }))
public class CareHistory extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }
}
