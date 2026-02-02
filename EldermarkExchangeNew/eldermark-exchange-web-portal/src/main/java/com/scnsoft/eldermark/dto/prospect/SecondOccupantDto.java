package com.scnsoft.eldermark.dto.prospect;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.entity.prospect.Veteran;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SecondOccupantDto {
    private AddressDto address;
    @NotEmpty
    @Size(max = 256)
    private String firstName;
    @NotEmpty
    @Size(max = 256)
    private String lastName;
    @Size(max = 256)
    private String middleName;
    private String fullName;
    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String cellPhone;
    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String email;
    @Size(max = 256)
    private String insurancePaymentPlan;
    private Long insuranceNetworkId;
    @NotNull
    private Long genderId;
    private String gender;
    @Pattern(regexp = ValidationRegExpConstants.SSN_REGEXP)
    private String ssn;
    private Long maritalStatusId;
    private String maritalStatus;
    private Long raceId;
    private String race;
    @NotNull
    private String birthDate;
    private Veteran veteranStatusName;
    private String veteranStatusTitle;

    private MultipartFile avatar;
    private Long avatarId;
    private String avatarName;
    private Boolean shouldRemoveAvatar;

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getInsurancePaymentPlan() {
        return insurancePaymentPlan;
    }

    public void setInsurancePaymentPlan(String insurancePaymentPlan) {
        this.insurancePaymentPlan = insurancePaymentPlan;
    }

    public Long getInsuranceNetworkId() {
        return insuranceNetworkId;
    }

    public void setInsuranceNetworkId(Long insuranceNetworkId) {
        this.insuranceNetworkId = insuranceNetworkId;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Long getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(Long maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
    }

    public Long getRaceId() {
        return raceId;
    }

    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Veteran getVeteranStatusName() {
        return veteranStatusName;
    }

    public void setVeteranStatusName(Veteran veteranStatusName) {
        this.veteranStatusName = veteranStatusName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getVeteranStatusTitle() {
        return veteranStatusTitle;
    }

    public void setVeteranStatusTitle(String veteranStatusTitle) {
        this.veteranStatusTitle = veteranStatusTitle;
    }

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public Boolean getShouldRemoveAvatar() {
        return shouldRemoveAvatar;
    }

    public void setShouldRemoveAvatar(Boolean shouldRemoveAvatar) {
        this.shouldRemoveAvatar = shouldRemoveAvatar;
    }
}
