package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "AdmitIntakeResidentDate")
@Immutable
public class AdmitIntakeClientDate {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "resident_id")
    private Long clientId;

    @Column(name = "admit_intake_date")
    private Instant admitIntakeDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Instant getAdmitIntakeDate() {
        return admitIntakeDate;
    }

    public void setAdmitIntakeDate(Instant admitIntakeDate) {
        this.admitIntakeDate = admitIntakeDate;
    }
}
