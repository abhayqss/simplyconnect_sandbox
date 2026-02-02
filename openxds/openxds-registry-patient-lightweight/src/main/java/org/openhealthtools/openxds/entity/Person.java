package org.openhealthtools.openxds.entity;


import org.hibernate.annotations.Cascade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Person extends StringLegacyIdAwareEntity {
    @Column(name = "legacy_table", nullable = false, length = 25)
    private String legacyTable;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<Name> names = new ArrayList<Name>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<PersonAddress> addresses = new ArrayList<PersonAddress>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<PersonTelecom> telecoms = new ArrayList<PersonTelecom>();;

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }

    public List<Name> getNames() {
        return names;
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    public List<PersonAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PersonAddress> addresses) {
        this.addresses = addresses;
    }

    public List<PersonTelecom> getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(List<PersonTelecom> telecoms) {
        this.telecoms = telecoms;
    }
}
