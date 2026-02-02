package com.scnsoft.eldermark.dto.client;

import com.scnsoft.eldermark.entity.document.CcdCode;

public class HouseHoldMemberListItemDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String relationship;
    private String birthDate;
    private String socialSecurityNumber;
    private CcdCode gender;
    private CcdCode genderIdentity;
    private String phone;
    private boolean isHouseholdHead;
    private boolean isActive;
    private Long date;

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

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public CcdCode getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(CcdCode genderIdentity) {
        this.genderIdentity = genderIdentity;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean getIsHouseholdHead() {
        return isHouseholdHead;
    }

    public void setIsHouseholdHead(boolean isHouseholdHead) {
        this.isHouseholdHead = isHouseholdHead;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
