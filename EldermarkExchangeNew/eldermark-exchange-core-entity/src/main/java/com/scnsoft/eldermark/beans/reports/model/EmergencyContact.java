package com.scnsoft.eldermark.beans.reports.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.*;

public class EmergencyContact {

    @JsonProperty(EMERGENCY_CONTACT_FIRST_NAME)
    private String firstName;

    @JsonProperty(EMERGENCY_CONTACT_LAST_NAME)
    private String lastName;

    @JsonProperty(EMERGENCY_CONTACT_PHONE_NUMBER)
    private String phoneNumber;

    @JsonProperty(EMERGENCY_CONTACT_ADDRESS_STREET)
    private String street;

    @JsonProperty(EMERGENCY_CONTACT_ADDRESS_CITY)
    private String city;

    @JsonProperty(EMERGENCY_CONTACT_ADDRESS_STATE)
    private String state;

    @JsonProperty(EMERGENCY_CONTACT_ADDRESS_ZIP_CODE)
    private String zipCode;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
