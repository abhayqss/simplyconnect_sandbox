package com.scnsoft.eldermark.api.shared.dto.adt;

import com.scnsoft.eldermark.api.shared.dto.adt.datatype.DLNDriverSLicenseNumberDto;
import com.scnsoft.eldermark.api.shared.dto.adt.datatype.XADPatientAddressDto;
import com.scnsoft.eldermark.api.shared.dto.adt.datatype.XTNPhoneNumberDto;

import java.util.Date;
import java.util.List;

public class PIDPatientIdentificationSegmentDto implements SegmentDto {
    private List<String> patientIdentifiers;
    private List<String> patientNames;
    private List<String> mothersMaidenNames;
    private Date dateTimeOfBirth;
    private String sex;
    private List<String> patientAliases;
    private List<String> races;
    private List<XADPatientAddressDto> patientAddresses;
    private List<XTNPhoneNumberDto> phoneNumbersHome;
    private List<XTNPhoneNumberDto> phoneNumbersBusiness;
    private String primaryLanguage;
    private String maritalStatus;
    private String religion;
    private String patientAccountNumber;
    private String ssnNumberPatient;
    private DLNDriverSLicenseNumberDto driverLicenseNumber;
    private List<String> motherIdentifiers;
    private List<String> etnicGroups;
    private String birthPlace;
    private Integer birthOrder;
    private List<String> citizenships;
    private String veteransMilitaryStatus;
    private String nationality;
    private Date deathDateTime;
    private Boolean deathIndicator;

    public List<String> getPatientIdentifiers() {
        return patientIdentifiers;
    }

    public void setPatientIdentifiers(List<String> patientIdentifiers) {
        this.patientIdentifiers = patientIdentifiers;
    }

    public List<String> getPatientNames() {
        return patientNames;
    }

    public void setPatientNames(List<String> patientNames) {
        this.patientNames = patientNames;
    }

    public List<String> getMothersMaidenNames() {
        return mothersMaidenNames;
    }

    public void setMothersMaidenNames(List<String> mothersMaidenNames) {
        this.mothersMaidenNames = mothersMaidenNames;
    }

    public List<String> getPatientAliases() {
        return patientAliases;
    }

    public void setPatientAliases(List<String> patientAliases) {
        this.patientAliases = patientAliases;
    }

    public List<String> getRaces() {
        return races;
    }

    public void setRaces(List<String> races) {
        this.races = races;
    }

    public List<XTNPhoneNumberDto> getPhoneNumbersHome() {
        return phoneNumbersHome;
    }

    public void setPhoneNumbersHome(List<XTNPhoneNumberDto> phoneNumbersHome) {
        this.phoneNumbersHome = phoneNumbersHome;
    }

    public List<XTNPhoneNumberDto> getPhoneNumbersBusiness() {
        return phoneNumbersBusiness;
    }

    public void setPhoneNumbersBusiness(List<XTNPhoneNumberDto> phoneNumbersBusiness) {
        this.phoneNumbersBusiness = phoneNumbersBusiness;
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

    public DLNDriverSLicenseNumberDto getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(DLNDriverSLicenseNumberDto driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public List<String> getMotherIdentifiers() {
        return motherIdentifiers;
    }

    public void setMotherIdentifiers(List<String> motherIdentifiers) {
        this.motherIdentifiers = motherIdentifiers;
    }

    public List<String> getEtnicGroups() {
        return etnicGroups;
    }

    public void setEtnicGroups(List<String> etnicGroups) {
        this.etnicGroups = etnicGroups;
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

    public List<String> getCitizenships() {
        return citizenships;
    }

    public void setCitizenships(List<String> citizenships) {
        this.citizenships = citizenships;
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

    public Boolean getDeathIndicator() {
        return deathIndicator;
    }

    public void setDeathIndicator(Boolean deathIndicator) {
        this.deathIndicator = deathIndicator;
    }

    public Date getDateTimeOfBirth() {
        return dateTimeOfBirth;
    }

    public void setDateTimeOfBirth(Date dateTimeOfBirth) {
        this.dateTimeOfBirth = dateTimeOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<XADPatientAddressDto> getPatientAddresses() {
        return patientAddresses;
    }

    public void setPatientAddresses(List<XADPatientAddressDto> patientAddresses) {
        this.patientAddresses = patientAddresses;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSsnNumberPatient() {
        return ssnNumberPatient;
    }

    public void setSsnNumberPatient(String ssnNumberPatient) {
        this.ssnNumberPatient = ssnNumberPatient;
    }
}
