package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@AttributeOverride(name = "legacyTable", column = @Column(name = "legacy_table", nullable = false, length = 25))
public class Person extends StringLegacyTableAwareEntity {

    @ManyToOne
    @JoinColumn(name = "type_code_id")
    private CcdCode code;

    @OneToMany(mappedBy = "person")
    private List<Name> names;

    @OneToMany(mappedBy = "person")
    private List<PersonAddress> addresses;

    @OneToMany(mappedBy = "person")
    private List<PersonTelecom> telecoms;

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
}
