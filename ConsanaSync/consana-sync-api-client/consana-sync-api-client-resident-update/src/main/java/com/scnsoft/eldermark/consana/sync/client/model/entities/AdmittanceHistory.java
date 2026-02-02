package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ResidentAdmittanceHistory")
public class AdmittanceHistory extends BaseReadOnlyEntity {

    @Column(name = "admit_date")
    private Instant admitDate;

    @Column(name = "discharge_date")
    private Instant dischargeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;

    public Instant getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Instant admitDate) {
        this.admitDate = admitDate;
    }

    public Instant getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Instant dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
