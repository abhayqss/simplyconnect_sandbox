package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.basic.DisplayablePrimaryFocusAwareEntity;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Deprecated
@Immutable
@Entity
public class ServicesTreatmentApproach extends DisplayablePrimaryFocusAwareEntity {
    public ServicesTreatmentApproach(Long id, String displayName, String key, Long primaryFocusId) {
        super(id, displayName, key, primaryFocusId);
    }

    public ServicesTreatmentApproach() {
    }

    @Column(name = "can_additional_clinical_info_be_shared")
    private boolean canAdditionalClinicalInfoBeShared;

    public boolean getCanAdditionalClinicalInfoBeShared() {
        return canAdditionalClinicalInfoBeShared;
    }

    public void setCanAdditionalclinicalInfoBeShared(boolean canAdditionalClinicalInfoBeShared) {
        this.canAdditionalClinicalInfoBeShared = canAdditionalClinicalInfoBeShared;
    }
}
