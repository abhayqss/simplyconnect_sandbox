package com.scnsoft.eldermark.entity.medication;

import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.Instant;

@Entity
public class MedicationReport extends LegacyTableAwareEntity {

    @Column(length = 15)
    private String dosage;

    @Column(name = "indicated_for")
    private String indicatedFor;

    @Column
    private String schedule;

    @Column(name = "effective_date")
    private Instant effectiveDate;

    @Column(length = 20)
    private String origin;

    @Column(name = "administer_by_nurse_only")
    private boolean administerByNurseOnly;

    @OneToOne
    @JoinColumn(name = "medication_id", insertable = false, updatable = false)
    private ClientMedication clientMedication;

    @OneToOne
    @JoinColumn(name = "medication_id")
    private Medication medication;

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getIndicatedFor() {
        return indicatedFor;
    }

    public void setIndicatedFor(String indicatedFor) {
        this.indicatedFor = indicatedFor;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Instant getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Instant effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public boolean isAdministerByNurseOnly() {
        return administerByNurseOnly;
    }

    public void setAdministerByNurseOnly(boolean administerByNurseOnly) {
        this.administerByNurseOnly = administerByNurseOnly;
    }

    public ClientMedication getClientMedication() {
        return clientMedication;
    }

    public void setClientMedication(ClientMedication medication) {
        this.clientMedication = medication;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }
}
