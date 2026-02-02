package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.facesheet.ContactWithRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ResMedProfessional")
public class ClientMedProfessional extends LegacyIdAwareEntity implements ContactWithRole {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "med_professional_id")
    private MedicalProfessional medicalProfessional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Client client;

    @Column(name = "rank")
    private Integer rank;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "medical_professional_role_id")
    private MedicalProfessionalRole medicalProfessionalRole;

    public MedicalProfessional getMedicalProfessional() {
        return medicalProfessional;
    }

    public void setMedicalProfessional(MedicalProfessional medicalProfessional) {
        this.medicalProfessional = medicalProfessional;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
    public String getCommuntiyName() {
        if (medicalProfessional != null) {
            return medicalProfessional.getOrganizationName();
        }
        return null;
    }
}
