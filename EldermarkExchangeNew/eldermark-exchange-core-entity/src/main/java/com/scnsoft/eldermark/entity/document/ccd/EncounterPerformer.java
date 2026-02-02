package com.scnsoft.eldermark.entity.document.ccd;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@Table(name = "EncounterPerformer")
public class EncounterPerformer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person performer;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "database_id")
    private Organization organization;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "provider_code_id")
    private CcdCode providerCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    public Person getPerformer() {
        return performer;
    }

    public void setPerformer(Person performer) {
        this.performer = performer;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public CcdCode getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(CcdCode providerCode) {
        this.providerCode = providerCode;
    }

}
