package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.community.Community;

@Entity
public class Author extends LegacyTableAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "time")
    private Date time;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = true)
    private Client client;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
