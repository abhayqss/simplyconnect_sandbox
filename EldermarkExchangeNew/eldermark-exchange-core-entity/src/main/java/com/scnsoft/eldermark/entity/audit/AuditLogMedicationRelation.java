package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.medication.Medication;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Medication")
public class AuditLogMedicationRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "medication_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Medication medication;

    @Column(name = "medication_id", nullable = false)
    private Long medicationId;

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(medicationId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.MEDICATION;
    }
}
