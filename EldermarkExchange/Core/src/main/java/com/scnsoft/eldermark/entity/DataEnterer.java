package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "DataEnterer")
public class DataEnterer extends BasicEntity {
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne(mappedBy = "dataEnterer")
    private Resident resident;

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
}
