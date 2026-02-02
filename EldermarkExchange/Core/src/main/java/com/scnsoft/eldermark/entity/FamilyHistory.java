package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class FamilyHistory extends BasicEntity {
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
    private Resident resident;

    public CcdCode getRelatedSubjectCode() {
        return relatedSubjectCode;
    }

    public void setRelatedSubjectCode(CcdCode relatedSubjectCode) {
        this.relatedSubjectCode = relatedSubjectCode;
    }

//    public FamilyMemberPerson getPersonInformation() {
//        return personInformation;
//    }
//
//    public void setPersonInformation(FamilyMemberPerson personInformation) {
//        this.personInformation = personInformation;
//    }

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

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
