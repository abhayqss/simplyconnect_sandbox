package com.scnsoft.eldermark.entity;

import java.util.List;

import javax.persistence.*;

import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@AttributeOverride(name = "legacyTable", column = @Column(name = "legacy_table", nullable = false, length = 25))
public class Person extends StringLegacyTableAwareEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "type_code_id")
    private CcdCode code;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
    private List<Name> names;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
    private List<PersonAddress> addresses;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
    private List<PersonTelecom> telecoms;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "person")
    private Employee employee;
  
    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
