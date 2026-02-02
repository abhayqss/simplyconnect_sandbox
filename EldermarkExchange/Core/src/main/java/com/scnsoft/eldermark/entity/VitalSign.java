package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "VitalSign", indexes = {
        @Index(name = "IX_vitalsign_legacy_id", columnList = "legacy_id"),
        @Index(name = "IX_vitalsign_resident", columnList = "resident_id")
})
public class VitalSign extends StringLegacyIdAwareEntity {
    @Column(name = "effective_time")
    private Date effectiveTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vitalSign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VitalSignObservation> vitalSignObservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public List<VitalSignObservation> getVitalSignObservations() {
        return vitalSignObservations;
    }

    public void setVitalSignObservations(List<VitalSignObservation> vitalSignObservations) {
        this.vitalSignObservations = vitalSignObservations;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
