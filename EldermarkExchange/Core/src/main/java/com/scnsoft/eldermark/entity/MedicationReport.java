package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "MedicationReport")
public class MedicationReport extends LegacyIdAwareEntity {

    @Column(name = "indicated_for")
    private String indicatedFor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medication medication;

    public String getIndicatedFor() {
        return indicatedFor;
    }

    public void setIndicatedFor(String indicatedFor) {
        this.indicatedFor = indicatedFor;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }
}
