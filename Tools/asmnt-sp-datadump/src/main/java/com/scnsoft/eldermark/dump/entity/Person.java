package com.scnsoft.eldermark.dump.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Person")
public class Person extends BasicEntity {

    private static final long serialVersionUID = 1L;

//    @ManyToOne
//    @JoinColumn(name = "type_code_id")
//    private CcdCode code;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
//    private List<Name> names;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
    private List<PersonAddress> addresses;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
//    private List<PersonTelecom> telecoms;


    public List<PersonAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PersonAddress> addresses) {
        this.addresses = addresses;
    }
}
