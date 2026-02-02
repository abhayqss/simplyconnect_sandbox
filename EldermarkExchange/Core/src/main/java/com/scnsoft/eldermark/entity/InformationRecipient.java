package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "InformationRecipient")
public class InformationRecipient extends BasicEntity {
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

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
