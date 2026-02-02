package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class Guardian extends LegacyIdAwareEntity implements ContactWithRelationship {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "relationship_code_id")
    private CcdCode relationship;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name="person_id")
    private Person person;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
