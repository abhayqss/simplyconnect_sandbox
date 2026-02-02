package com.scnsoft.eldermark.shared.carecoordination;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * This DTO is intended to represent patient info.
 * Created by pzhurba on 05-Oct-15.
 */
@Deprecated
public class AdtEventDto {
    private String patientIdentifier;
    private String mothersMaidenName;
    private String patientAlias;
    private String race;
    private String primaryLanguage;
    private String religion;
    private String patientAccountNumber;
    private String driverLicenseNumber;
    private String motherIdentifier;
    private String etnicGroup;
    private String birthPlace;
    private Integer birthOrder;
    private String citizenship;
    private String veteransMilitaryStatus;
    private String nationality;
    private Date deathDateTime;
    private Boolean deathIndicator;

    /*EVN segment fields */
    private String eventTypeCode;
    private Date recordedDateTime;
    private String eventReasonCode;
    private Date eventOccured;

    private String phoneNumberHome;
    private String phoneNumberBusiness;
//
//    public AdtEventDto(PatientDto patientDto) {
//        setId(patientDto.getId());
//        setBirthDate(patientDto.getBirthDate());
//        setSsn(patientDto.getSsn());
//        setGender(patientDto.getGender());
//        setMaritalStatus(patientDto.getMaritalStatus());
//        setAddress(patientDto.getAddress());
//        setOrganization(patientDto.getOrganization());
//        setOrganizationId(patientDto.getOrganizationId());
//        setCommunity(patientDto.getCommunity());
//        setCommunityId(patientDto.getCommunityId());
//        setEditable(patientDto.getEditable());
//        setPhone(patientDto.getPhone());
//        setEmail(patientDto.getEmail());
//        setActive(patientDto.getActive());
//        setHashKey(patientDto.getHashKey());
//    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public void setPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
    }

    public String getMothersMaidenName() {
        return mothersMaidenName;
    }

    public void setMothersMaidenName(String mothersMaidenName) {
        this.mothersMaidenName = mothersMaidenName;
    }

    public String getPatientAlias() {
        return patientAlias;
    }

    public void setPatientAlias(String patientAlias) {
        this.patientAlias = patientAlias;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPatientAccountNumber() {
        return patientAccountNumber;
    }

    public void setPatientAccountNumber(String patientAccountNumber) {
        this.patientAccountNumber = patientAccountNumber;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getMotherIdentifier() {
        return motherIdentifier;
    }

    public void setMotherIdentifier(String motherIdentifier) {
        this.motherIdentifier = motherIdentifier;
    }

    public String getEtnicGroup() {
        return etnicGroup;
    }

    public void setEtnicGroup(String etnicGroup) {
        this.etnicGroup = etnicGroup;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Integer getBirthOrder() {
        return birthOrder;
    }

    public void setBirthOrder(Integer birthOrder) {
        this.birthOrder = birthOrder;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getVeteransMilitaryStatus() {
        return veteransMilitaryStatus;
    }

    public void setVeteransMilitaryStatus(String veteransMilitaryStatus) {
        this.veteransMilitaryStatus = veteransMilitaryStatus;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Date getDeathDateTime() {
        return deathDateTime;
    }

    public void setDeathDateTime(Date deathDateTime) {
        this.deathDateTime = deathDateTime;
    }

    public String getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(String eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }

    public Date getRecordedDateTime() {
        return recordedDateTime;
    }

    public void setRecordedDateTime(Date recordedDateTime) {
        this.recordedDateTime = recordedDateTime;
    }

    public String getEventReasonCode() {
        return eventReasonCode;
    }

    public void setEventReasonCode(String eventReasonCode) {
        this.eventReasonCode = eventReasonCode;
    }

    public Date getEventOccured() {
        return eventOccured;
    }

    public void setEventOccured(Date eventOccured) {
        this.eventOccured = eventOccured;
    }

    public String getPhoneNumberHome() {
        return phoneNumberHome;
    }

    public void setPhoneNumberHome(String phoneNumberHome) {
        this.phoneNumberHome = phoneNumberHome;
    }

    public String getPhoneNumberBusiness() {
        return phoneNumberBusiness;
    }

    public void setPhoneNumberBusiness(String phoneNumberBusiness) {
        this.phoneNumberBusiness = phoneNumberBusiness;
    }

    public Boolean getDeathIndicator() {
        return deathIndicator;
    }

    public void setDeathIndicator(Boolean deathIndicator) {
        this.deathIndicator = deathIndicator;
    }
}
