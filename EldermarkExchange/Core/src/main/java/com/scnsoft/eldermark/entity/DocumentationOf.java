package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class DocumentationOf extends BasicEntity implements ContactWithRole {
    @Column(name = "effective_time_low")
    private Date effectiveTimeLow;

    @Column(name = "effective_time_high")
    private Date effectiveTimeHigh;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="DocumentationOf_Person",
               joinColumns = @JoinColumn(name="documentation_of_id"),
               inverseJoinColumns = @JoinColumn(name="person_id") )
    private List<Person> persons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    public Date getEffectiveTimeLow() {
        return effectiveTimeLow;
    }

    public void setEffectiveTimeLow(Date effectiveTimeLow) {
        this.effectiveTimeLow = effectiveTimeLow;
    }

    public Date getEffectiveTimeHigh() {
        return effectiveTimeHigh;
    }

    public void setEffectiveTimeHigh(Date effectiveTimeHigh) {
        this.effectiveTimeHigh = effectiveTimeHigh;
    }

    @Override
    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @Override
    public String getRole() {
        return "Clinician";
    }

    @Override
    public String getNpi() {
        return null;
    }

    @Override
    public String getOrganizationName() {
        return null;
    }
}
