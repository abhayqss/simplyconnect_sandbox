package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "SourceDatabase")
@Cacheable
public class Database extends BaseReadOnlyEntity {

    @Column(name = "consana_xowning_id")
    private String consanaXOwningId;

    @Column(name = "name", length = 256, nullable = false, unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "address_and_contacts_id", referencedColumnName = "id", columnDefinition = "int")
    private SourceDatabaseAddressAndContacts addressAndContacts;

    public Database() {
    }

    public Database(Long id, String consanaXOwningId) {
        super(id);
        this.consanaXOwningId = consanaXOwningId;
    }

    public String getConsanaXOwningId() {
        return consanaXOwningId;
    }

    public void setConsanaXOwningId(String consanaXOwningId) {
        this.consanaXOwningId = consanaXOwningId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SourceDatabaseAddressAndContacts getAddressAndContacts() {
        return addressAndContacts;
    }

    public void setAddressAndContacts(SourceDatabaseAddressAndContacts addressAndContacts) {
        this.addressAndContacts = addressAndContacts;
    }
}
