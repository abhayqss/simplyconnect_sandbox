package com.scnsoft.eldermark.dto.pointclickcare.model.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scnsoft.eldermark.dto.pointclickcare.model.PCCSwitcherooCodeableConcept;

import java.time.Instant;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PCCPatient {

    private LocalDate admissionDate;
    private LocalDate birthDate;
    private String citizenship;
    private Instant deathDateTime;
    private Boolean deceased;
    private LocalDate dischargeDate;
    private String email;
    private Long facId;
    private String firstName;
    private Gender gender;
    private String lastName;
    private String medicaidNumber;
    private String medicalRecordNumber;
    private String medicareNumber;
    private String orgUuid;
    private Long patientId;

    private PCCSwitcherooCodeableConcept raceCode;

    //"New" "Current" "Discharged".
    private String patientStatus;
    private String preferredName;
    private String prefix;
    private String suffix;

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public Instant getDeathDateTime() {
        return deathDateTime;
    }

    public void setDeathDateTime(Instant deathDateTime) {
        this.deathDateTime = deathDateTime;
    }

    public Boolean getDeceased() {
        return deceased;
    }

    public void setDeceased(Boolean deceased) {
        this.deceased = deceased;
    }

    public LocalDate getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDate dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getOrgUuid() {
        return orgUuid;
    }

    public void setOrgUuid(String orgUuid) {
        this.orgUuid = orgUuid;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public PCCSwitcherooCodeableConcept getRaceCode() {
        return raceCode;
    }

    public void setRaceCode(PCCSwitcherooCodeableConcept raceCode) {
        this.raceCode = raceCode;
    }

    public String getPatientStatus() {
        return patientStatus;
    }

    public void setPatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public enum Gender {
        MALE,
        FEMALE,
        UNKNOWN
    }
}
