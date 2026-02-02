package com.scnsoft.eldermark.dto.lab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientSummaryLabsAdaptDto {
    @NotNull
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    @NotNull
    private Long genderId;
    private String genderTitle;
    @NotNull
    private Long raceId;
    private String raceTitle;
    private String birthDate;
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String phone;
    @Size(max = 9)
    private String ssn;
    private AddressDto address;
    @Size(max = 256)
    @NotEmpty
    private String insuranceNetwork;
    @Size(max = 256)
    @NotEmpty
    private String policyNumber;
    @Size(max = 256)
    @NotEmpty
    private String policyHolderRelationName;
    private String policyHolderRelationTitle;
    private String policyHolderName;
    private String policyHolderDOB;
    private boolean canView;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public String getGenderTitle() {
        return genderTitle;
    }

    public void setGenderTitle(String genderTitle) {
        this.genderTitle = genderTitle;
    }

    public Long getRaceId() {
        return raceId;
    }

    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public String getRaceTitle() {
        return raceTitle;
    }

    public void setRaceTitle(String raceTitle) {
        this.raceTitle = raceTitle;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getInsuranceNetwork() {
        return insuranceNetwork;
    }

    public void setInsuranceNetwork(String insuranceNetwork) {
        this.insuranceNetwork = insuranceNetwork;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyHolderRelationName() {
        return policyHolderRelationName;
    }

    public void setPolicyHolderRelationName(String policyHolderRelationName) {
        this.policyHolderRelationName = policyHolderRelationName;
    }

    public String getPolicyHolderRelationTitle() {
        return policyHolderRelationTitle;
    }

    public void setPolicyHolderRelationTitle(String policyHolderRelationTitle) {
        this.policyHolderRelationTitle = policyHolderRelationTitle;
    }

    public String getPolicyHolderName() {
        return policyHolderName;
    }

    public void setPolicyHolderName(String policyHolderName) {
        this.policyHolderName = policyHolderName;
    }

    public String getPolicyHolderDOB() {
        return policyHolderDOB;
    }

    public void setPolicyHolderDOB(String policyHolderDOB) {
        this.policyHolderDOB = policyHolderDOB;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public boolean getCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }
}
