package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class Guardian extends LegacyIdAwareEntity implements ContactWithRelationship {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @ManyToOne
    @JoinColumn(name = "relationship_code_id")
    private CcdCode relationship;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name="person_id")
    private Person person;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public CcdCode getRelationship() {
        return relationship;
    }

    public void setRelationship(CcdCode relationships) {
        this.relationship = relationships;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
