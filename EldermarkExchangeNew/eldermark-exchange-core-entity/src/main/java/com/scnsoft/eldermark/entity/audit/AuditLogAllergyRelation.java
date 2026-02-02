package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Allergy")
public class AuditLogAllergyRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "allergy_observation_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AllergyObservation allergy;

    @Column(name = "allergy_observation_id", nullable = false)
    private Long allergyId;

    public AllergyObservation getAllergy() {
        return allergy;
    }

    public void setAllergy(AllergyObservation allergy) {
        this.allergy = allergy;
    }

    public Long getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(Long allergyId) {
        this.allergyId = allergyId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(allergyId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.ALLERGY;
    }
}
