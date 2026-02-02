package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "MedicalProfessionalRole")
public class MedicalProfessionalRole extends LegacyIdAwareEntity {

    @Column(name="description")
    private String description;

    @Column
    private Boolean inactive;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }
}
