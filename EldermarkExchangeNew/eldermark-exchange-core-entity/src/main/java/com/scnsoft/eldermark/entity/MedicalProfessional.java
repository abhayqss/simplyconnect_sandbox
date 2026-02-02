package com.scnsoft.eldermark.entity;

import javax.persistence.*;

import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.community.Community;

@Entity
@Table(name = "MedicalProfessional",
        uniqueConstraints = @UniqueConstraint(columnNames = {"legacy_id", "database_id"}))
public class MedicalProfessional extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "organization_name", length = 40)
    private String organizationName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(length = 10)
    private String npi;

    @Column(length = 30)
    private String speciality;

    @Column
    private Boolean inactive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Community community;

    @Column(name = "ext_pharmacy_id")
    private String extPharmacyId;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public String getExtPharmacyId() {
        return extPharmacyId;
    }

    public void setExtPharmacyId(String extPharmacyId) {
        this.extPharmacyId = extPharmacyId;
    }
}
