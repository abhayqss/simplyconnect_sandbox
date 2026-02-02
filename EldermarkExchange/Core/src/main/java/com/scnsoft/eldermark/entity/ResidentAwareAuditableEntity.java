package com.scnsoft.eldermark.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class ResidentAwareAuditableEntity extends AuditableEntity {

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Resident resident;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
