package com.scnsoft.eldermark.dto.pointclickcare.filter.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;

import java.time.LocalDate;

public class PCCPatientFilterExactMatchCriteria {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate birthDate;
    private Long facId;
    private String firstName;
    private PCCPatientDetails.Gender gender;
    private String healthCardNumber;
    private String lastName;
    private String medicaidNumber;
    private String medicareNumber;
    private String socialBeneficiaryIdentifier;

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Long getFacId() {
        return facId;
    }

    public void setFacId(Long facId) {
        this.facId = facId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public PCCPatientDetails.Gender getGender() {
        return gender;
    }

    public void setGender(PCCPatientDetails.Gender gender) {
        this.gender = gender;
    }

    public String getHealthCardNumber() {
        return healthCardNumber;
    }

    public void setHealthCardNumber(String healthCardNumber) {
        this.healthCardNumber = healthCardNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getSocialBeneficiaryIdentifier() {
        return socialBeneficiaryIdentifier;
    }

    public void setSocialBeneficiaryIdentifier(String socialBeneficiaryIdentifier) {
        this.socialBeneficiaryIdentifier = socialBeneficiaryIdentifier;
    }
}
