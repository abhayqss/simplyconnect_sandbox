package com.scnsoft.eldermark.beans.reports.model;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_BIRTH_DATE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_DATE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_FIRST_NAME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_GENDER;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_GENDER_IDENTITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_GENDER_IDENTITY_2;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_HOUSEHOLD_HEAD;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_LAST_NAME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_MIDDLE_NAME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_PHONE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_RELATIONSHIP;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_SSN;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.HOUSEHOLD_MEMBER_STATUS;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HouseholdMemberAssessmentDto {

    @JsonProperty(HOUSEHOLD_MEMBER_FIRST_NAME)
    private String firstName;

    @JsonProperty(HOUSEHOLD_MEMBER_MIDDLE_NAME)
    private String middleName;

    @JsonProperty(HOUSEHOLD_MEMBER_LAST_NAME)
    private String lastName;

    @JsonProperty(HOUSEHOLD_MEMBER_RELATIONSHIP)
    private String relationship;

    @JsonProperty(HOUSEHOLD_MEMBER_BIRTH_DATE)
    private String birthDate;

    @JsonProperty(HOUSEHOLD_MEMBER_SSN)
    private String socialSecurityNumber;

    @JsonProperty(HOUSEHOLD_MEMBER_GENDER)
    private String gender;

    @JsonProperty(HOUSEHOLD_MEMBER_GENDER_IDENTITY)
    private String genderIdentity;

    @JsonProperty(HOUSEHOLD_MEMBER_GENDER_IDENTITY_2)
    private String genderIdentity2;

    @JsonProperty(HOUSEHOLD_MEMBER_PHONE)
    private String phone;

    @JsonProperty(HOUSEHOLD_MEMBER_HOUSEHOLD_HEAD)
    private String isHouseholdHead;

    @JsonProperty(HOUSEHOLD_MEMBER_STATUS)
    private String isActive;

    @JsonProperty(HOUSEHOLD_MEMBER_DATE)
    private String date;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(String genderIdentity) {
        this.genderIdentity = genderIdentity;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String isHouseholdHead() {
        return isHouseholdHead;
    }

    public void setHouseholdHead(String householdHead) {
        isHouseholdHead = householdHead;
    }

    public String isActive() {
        return isActive;
    }

    public void setActive(String active) {
        isActive = active;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
