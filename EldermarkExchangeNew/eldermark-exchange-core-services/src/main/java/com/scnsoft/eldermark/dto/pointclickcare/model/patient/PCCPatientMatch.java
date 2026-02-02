package com.scnsoft.eldermark.dto.pointclickcare.model.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PCCPatientMatch {
    private LocalDate birthDate;
    private Long facId;
    private String firstName;
    private PCCPatientDetails.Gender gender;
    private String healthCardNumber;
    private String lastName;
    private String medicaidNumber;
    private String medicareNumber;
    private String middleName;
    private Long patientId;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getSocialBeneficiaryIdentifier() {
        return socialBeneficiaryIdentifier;
    }

    public void setSocialBeneficiaryIdentifier(String socialBeneficiaryIdentifier) {
        this.socialBeneficiaryIdentifier = socialBeneficiaryIdentifier;
    }
}
