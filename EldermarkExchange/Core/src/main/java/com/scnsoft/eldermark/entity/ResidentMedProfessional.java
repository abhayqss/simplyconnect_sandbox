package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ResMedProfessional")
public class ResidentMedProfessional extends LegacyIdAwareEntity implements ContactWithRole{
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "med_professional_id")
    private MedicalProfessional medicalProfessional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;

    @Column(name = "rank")
    private Integer rank;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "medical_professional_role_id")
    private MedicalProfessionalRole medicalProfessionalRole;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    public MedicalProfessional getMedicalProfessional() {
        return medicalProfessional;
    }

    public void setMedicalProfessional(MedicalProfessional medicalProfessional) {
        this.medicalProfessional = medicalProfessional;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public MedicalProfessionalRole getMedicalProfessionalRole() {
        return medicalProfessionalRole;
    }

    public void setMedicalProfessionalRole(MedicalProfessionalRole medicalProfessionalRole) {
        this.medicalProfessionalRole = medicalProfessionalRole;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public String getRole() {
        if (medicalProfessionalRole != null) {
            return medicalProfessionalRole.getDescription();
        }
        return null;
    }

    @Override
    public List<Person> getPersons() {
        List<Person> personList = new ArrayList<>();
        if (medicalProfessional != null && medicalProfessional.getPerson() != null) {
            personList.add(medicalProfessional.getPerson());
        }
        return personList;
    }

    @Override
    public String getNpi() {
        if (medicalProfessional != null) {
            return medicalProfessional.getNpi();
        }
        return null;
    }

    @Override
    public String getOrganizationName() {
        if (medicalProfessional != null) {
            return medicalProfessional.getOrganizationName();
        }
        return null;
    }
}
