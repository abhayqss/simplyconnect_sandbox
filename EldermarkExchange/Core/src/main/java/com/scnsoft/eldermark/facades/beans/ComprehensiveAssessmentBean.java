package com.scnsoft.eldermark.facades.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ComprehensiveAssessmentBean {

    @JsonProperty("Home phone number")
    private String homePhoneNumber;

    @JsonProperty("Cell phone number")
    private String cellPhoneNumber;

    @JsonProperty("Work phone number")
    private String workPhoneNumber;

    @JsonProperty("First name2")
    private String primaryCarePhysicianFirstName;

    @JsonProperty("Last Name2")
    private String primaryCarePhysicianLastName;

    @JsonProperty("Phone number1")
    private String primaryCarePhysicianPhone;

    @JsonProperty("Street2")
    private String pcpStreet;

    @JsonProperty("City2")
    private String pcpCity;

    @JsonProperty("State2")
    private String pcpState;

    @JsonProperty("Zip Code2")
    private String pcpZipCode;

    @JsonProperty("First name3")
    private String specialtyFirstName;

    @JsonProperty("Last Name3")
    private String specialtyLastName;

    @JsonProperty("Specialty")
    private String specialtyRole;

    @JsonProperty("Phone number2")
    private String specialtyPhone;

    @JsonProperty("Street3")
    private String specialtyStreet;

    @JsonProperty("City3")
    private String specialtyCity;

    @JsonProperty("State3")
    private String specialtyState;

    @JsonProperty("Zip Code3")
    private String specialtyZipCode;

    @JsonProperty("Name")
    private String pharmacyName;

    @JsonProperty("Phone number3")
    private String pharmacyPhone;

    @JsonProperty("Street4")
    private String pharmacyStreet;

    @JsonProperty("City4")
    private String pharmacyCity;

    @JsonProperty("State4")
    private String pharmacyState;

    @JsonProperty("Zip Code4")
    private String pharmacyZipCode;

    public String getHomePhoneNumber() {
        return homePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        this.homePhoneNumber = homePhoneNumber;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public String getWorkPhoneNumber() {
        return workPhoneNumber;
    }

    public void setWorkPhoneNumber(String workPhoneNumber) {
        this.workPhoneNumber = workPhoneNumber;
    }

    public String getPrimaryCarePhysicianFirstName() {
        return primaryCarePhysicianFirstName;
    }

    public void setPrimaryCarePhysicianFirstName(String primaryCarePhysicianFirstName) {
        this.primaryCarePhysicianFirstName = primaryCarePhysicianFirstName;
    }

    public String getPrimaryCarePhysicianLastName() {
        return primaryCarePhysicianLastName;
    }

    public void setPrimaryCarePhysicianLastName(String primaryCarePhysicianLastName) {
        this.primaryCarePhysicianLastName = primaryCarePhysicianLastName;
    }

    public String getPrimaryCarePhysicianPhone() {
        return primaryCarePhysicianPhone;
    }

    public void setPrimaryCarePhysicianPhone(String primaryCarePhysicianPhone) {
        this.primaryCarePhysicianPhone = primaryCarePhysicianPhone;
    }

    public String getPcpStreet() {
        return pcpStreet;
    }

    public void setPcpStreet(String pcpStreet) {
        this.pcpStreet = pcpStreet;
    }

    public String getPcpCity() {
        return pcpCity;
    }

    public void setPcpCity(String pcpCity) {
        this.pcpCity = pcpCity;
    }

    public String getPcpState() {
        return pcpState;
    }

    public void setPcpState(String pcpState) {
        this.pcpState = pcpState;
    }

    public String getPcpZipCode() {
        return pcpZipCode;
    }

    public void setPcpZipCode(String pcpZipCode) {
        this.pcpZipCode = pcpZipCode;
    }

    public String getSpecialtyFirstName() {
        return specialtyFirstName;
    }

    public void setSpecialtyFirstName(String specialtyFirstName) {
        this.specialtyFirstName = specialtyFirstName;
    }

    public String getSpecialtyLastName() {
        return specialtyLastName;
    }

    public void setSpecialtyLastName(String specialtyLastName) {
        this.specialtyLastName = specialtyLastName;
    }

    public String getSpecialtyRole() {
        return specialtyRole;
    }

    public void setSpecialtyRole(String specialtyRole) {
        this.specialtyRole = specialtyRole;
    }

    public String getSpecialtyPhone() {
        return specialtyPhone;
    }

    public void setSpecialtyPhone(String specialtyPhone) {
        this.specialtyPhone = specialtyPhone;
    }

    public String getSpecialtyStreet() {
        return specialtyStreet;
    }

    public void setSpecialtyStreet(String specialtyStreet) {
        this.specialtyStreet = specialtyStreet;
    }

    public String getSpecialtyCity() {
        return specialtyCity;
    }

    public void setSpecialtyCity(String specialtyCity) {
        this.specialtyCity = specialtyCity;
    }

    public String getSpecialtyState() {
        return specialtyState;
    }

    public void setSpecialtyState(String specialtyState) {
        this.specialtyState = specialtyState;
    }

    public String getSpecialtyZipCode() {
        return specialtyZipCode;
    }

    public void setSpecialtyZipCode(String specialtyZipCode) {
        this.specialtyZipCode = specialtyZipCode;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getPharmacyPhone() {
        return pharmacyPhone;
    }

    public void setPharmacyPhone(String pharmacyPhone) {
        this.pharmacyPhone = pharmacyPhone;
    }

    public String getPharmacyStreet() {
        return pharmacyStreet;
    }

    public void setPharmacyStreet(String pharmacyStreet) {
        this.pharmacyStreet = pharmacyStreet;
    }

    public String getPharmacyCity() {
        return pharmacyCity;
    }

    public void setPharmacyCity(String pharmacyCity) {
        this.pharmacyCity = pharmacyCity;
    }

    public String getPharmacyState() {
        return pharmacyState;
    }

    public void setPharmacyState(String pharmacyState) {
        this.pharmacyState = pharmacyState;
    }

    public String getPharmacyZipCode() {
        return pharmacyZipCode;
    }

    public void setPharmacyZipCode(String pharmacyZipCode) {
        this.pharmacyZipCode = pharmacyZipCode;
    }
}
