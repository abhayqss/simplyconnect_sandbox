package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "Custodian")
public class Custodian extends LegacyIdAwareEntity {
    @OneToOne
    @JoinColumn (name = "organization_id")
    private Organization organization;

    @OneToOne (mappedBy = "custodian")
    private Resident resident;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
