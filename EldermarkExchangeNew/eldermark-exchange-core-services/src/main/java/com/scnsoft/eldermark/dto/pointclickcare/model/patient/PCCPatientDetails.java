package com.scnsoft.eldermark.dto.pointclickcare.model.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scnsoft.eldermark.dto.pointclickcare.model.PCCSwitcherooCodeableConcept;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PCCPatientDetails {
    public static final String PATIENT_STATUS_NEW = "New";
    public static final String PATIENT_STATUS_CURRENT = "Current";
    public static final String PATIENT_STATUS_DISCHARGED = "Discharged";

    private LocalDate admissionDate;

    private Instant admissionDateTime;

    private LocalDate birthDate;
    private String citizenship;
    private Instant deathDateTime;
    private Boolean deceased;
    private LocalDate dischargeDate;
    private String email;
    private PCCSwitcherooCodeableConcept ethnicityCode;
    private Long facId;
    private String firstName;
    private Gender gender;
    private String languageCode;
    private String languageDesc;
    private String lastName;
    private PccPatientLegalMailingAddress legalMailingAddress;
    private String maidenName;
    private String maritalStatus;
    private String medicaidNumber;
    private String medicalRecordNumber;
    private String medicareBeneficiaryIdentifier;
    private String medicareNumber;
    private String middleName;
    private String orgUuid;
    private boolean outpatient;
    private Long patientId;

    private PCCSwitcherooCodeableConcept raceCode;

    //"New" "Current" "Discharged".
    private String patientStatus;
    private String preferredName;
    private String prefix;
    private String socialBeneficiaryIdentifier;
    private String suffix;
    private String ituPhone;
    private String phoneNumberType;

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Instant getAdmissionDateTime() {
        return admissionDateTime;
    }

    public void setAdmissionDateTime(Instant admissionDateTime) {
        this.admissionDateTime = admissionDateTime;
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

    public PCCSwitcherooCodeableConcept getEthnicityCode() {
        return ethnicityCode;
    }

    public void setEthnicityCode(PCCSwitcherooCodeableConcept ethnicityCode) {
        this.ethnicityCode = ethnicityCode;
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

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageDesc() {
        return languageDesc;
    }

    public void setLanguageDesc(String languageDesc) {
        this.languageDesc = languageDesc;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PccPatientLegalMailingAddress getLegalMailingAddress() {
        return legalMailingAddress;
    }

    public void setLegalMailingAddress(PccPatientLegalMailingAddress legalMailingAddress) {
        this.legalMailingAddress = legalMailingAddress;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
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

    public String getMedicareBeneficiaryIdentifier() {
        return medicareBeneficiaryIdentifier;
    }

    public void setMedicareBeneficiaryIdentifier(String medicareBeneficiaryIdentifier) {
        this.medicareBeneficiaryIdentifier = medicareBeneficiaryIdentifier;
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

    public String getOrgUuid() {
        return orgUuid;
    }

    public void setOrgUuid(String orgUuid) {
        this.orgUuid = orgUuid;
    }

    public boolean isOutpatient() {
        return outpatient;
    }

    public void setOutpatient(boolean outpatient) {
        this.outpatient = outpatient;
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

    public String getSocialBeneficiaryIdentifier() {
        return socialBeneficiaryIdentifier;
    }

    public void setSocialBeneficiaryIdentifier(String socialBeneficiaryIdentifier) {
        this.socialBeneficiaryIdentifier = socialBeneficiaryIdentifier;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getItuPhone() {
        return ituPhone;
    }

    public void setItuPhone(String ituPhone) {
        this.ituPhone = ituPhone;
    }

    public String getPhoneNumberType() {
        return phoneNumberType;
    }

    public void setPhoneNumberType(String phoneNumberType) {
        this.phoneNumberType = phoneNumberType;
    }

    public enum Gender {
        MALE,
        FEMALE,
        UNKNOWN
    }
}
