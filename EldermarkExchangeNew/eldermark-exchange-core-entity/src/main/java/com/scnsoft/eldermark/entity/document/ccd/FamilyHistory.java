package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class FamilyHistory extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "related_subject_code_id")
    private CcdCode relatedSubjectCode;

    @Column(name = "person_information_id")
    private String personInformationId;

    @ManyToOne
    @JoinColumn(name = "administrative_gender_code_id")
    private CcdCode administrativeGenderCode;

    @Column(name = "birth_time")
    private Date birthTime;

    @Column(name = "deceased_ind")
    private Boolean deceasedInd;

    @Column(name = "deceased_time")
    private Date deceasedTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "familyHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("effectiveTime desc")
    private List<FamilyHistoryObservation> familyHistoryObservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    public CcdCode getRelatedSubjectCode() {
        return relatedSubjectCode;
    }

    public void setRelatedSubjectCode(CcdCode relatedSubjectCode) {
        this.relatedSubjectCode = relatedSubjectCode;
    }

    public String getPersonInformationId() {
        return personInformationId;
    }

    public void setPersonInformationId(String personInformationId) {
        this.personInformationId = personInformationId;
    }

    public CcdCode getAdministrativeGenderCode() {
        return administrativeGenderCode;
    }

    public void setAdministrativeGenderCode(CcdCode administrativeGenderCode) {
        this.administrativeGenderCode = administrativeGenderCode;
    }

    public Date getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(Date birthTime) {
        this.birthTime = birthTime;
    }

    public Boolean getDeceasedInd() {
        return deceasedInd;
    }

    public void setDeceasedInd(Boolean deceasedInd) {
        this.deceasedInd = deceasedInd;
    }

    public Date getDeceasedTime() {
        return deceasedTime;
    }

    public void setDeceasedTime(Date deceasedTime) {
        this.deceasedTime = deceasedTime;
    }

    public List<FamilyHistoryObservation> getFamilyHistoryObservations() {
        return familyHistoryObservations;
    }

    public void setFamilyHistoryObservations(List<FamilyHistoryObservation> familyHistoryObservations) {
        this.familyHistoryObservations = familyHistoryObservations;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
