package com.scnsoft.eldermark.entity;

import javax.persistence.*;

/**
 * A person who provided information (e.g. family member of patient who could not speak).
 */
@Entity
@Table(name = "Informant")
public class Informant extends BasicEntity {
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(name = "is_personal_relation")
    private Boolean isPersonalRelation;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Boolean getPersonalRelation() {
        return isPersonalRelation;
    }

    public void setPersonalRelation(Boolean personalRelation) {
        isPersonalRelation = personalRelation;
    }
}
