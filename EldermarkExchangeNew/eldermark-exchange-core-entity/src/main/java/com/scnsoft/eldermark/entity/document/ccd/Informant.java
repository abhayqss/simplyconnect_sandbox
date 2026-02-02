package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.basic.BasicEntity;

/**
 * A person who provided information (e.g. family member of patient who could not speak).
 */
@Entity
@Table(name = "Informant")
public class Informant extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    @Column(name = "is_personal_relation")
    private Boolean isPersonalRelation;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Boolean getPersonalRelation() {
        return isPersonalRelation;
    }

    public void setPersonalRelation(Boolean personalRelation) {
        isPersonalRelation = personalRelation;
    }
}
