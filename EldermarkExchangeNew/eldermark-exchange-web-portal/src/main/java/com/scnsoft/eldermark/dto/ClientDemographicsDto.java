package com.scnsoft.eldermark.dto;

import java.util.Date;

public class ClientDemographicsDto {
    private Long gender;
    private Date birthDate;
    private Long maritalStatus;
    private String phone;
    private String cellPhone;
    private String email;
    private String organization;
    private Long organizationId;
    private String community;
    private Long communityId;
    private AddressDto address;
    private String risksdScore;

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Long getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Long maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getRisksdScore() {
        return risksdScore;
    }

    public void setRisksdScore(String risksdScore) {
        this.risksdScore = risksdScore;
    }

    public String getSharingData() {
        return sharingData;
    }

    public void setSharingData(String sharingData) {
        this.sharingData = sharingData;
    }

    private String sharingData;

}
