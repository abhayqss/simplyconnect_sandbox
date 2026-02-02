package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.facesheet.ContactWithRole;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.basic.BasicEntity;

@Entity
public class DocumentationOf extends BasicEntity implements ContactWithRole {
    private static final long serialVersionUID = 1L;

    @Column(name = "effective_time_low")
    private Date effectiveTimeLow;

    @Column(name = "effective_time_high")
    private Date effectiveTimeHigh;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "DocumentationOf_Person", joinColumns = @JoinColumn(name = "documentation_of_id"), inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<Person> persons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
    public String getCommuntiyName() {
        return getClient().getCommunity().getName();
    }
}
