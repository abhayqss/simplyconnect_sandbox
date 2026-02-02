package com.scnsoft.eldermark.mobile.dto.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import com.scnsoft.eldermark.web.commons.validation.Age;
import com.scnsoft.eldermark.web.commons.validation.AgeConstraintValidator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class EmployeeUpdateRequestDto {

    @JsonIgnore
    private Long id;

    @NotEmpty
    @Size(max = 256)
    private String firstName;

    @NotEmpty
    @Size(max = 256)
    private String lastName;

    @NotNull
    @Age(message = "The Care Team Member must be over 18 year old",
            value = 18,
            rules = {AgeConstraintValidator.Rule.GREATER_THAN, AgeConstraintValidator.Rule.EQUAL}
    )
    private LocalDate birthDate;

    private byte[] avatarData;
    private String avatarMimeType;
    private boolean shouldRemoveAvatar;

    @NotNull
    @Size(max = 256)
    private String street;

    @NotNull
    @Size(max = 256)
    private String city;

    @NotNull
    private Long stateId;

    @NotNull
    @Pattern(regexp = ValidationRegExpConstants.ZIP_CODE_REGEXP)
    private String zipCode;

    @NotNull
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String cellPhone;

    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String homePhone;

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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public byte[] getAvatarData() {
        return avatarData;
    }

    public void setAvatarData(byte[] avatarData) {
        this.avatarData = avatarData;
    }

    public String getAvatarMimeType() {
        return avatarMimeType;
    }

    public void setAvatarMimeType(String avatarMimeType) {
        this.avatarMimeType = avatarMimeType;
    }

    public boolean isShouldRemoveAvatar() {
        return shouldRemoveAvatar;
    }

    public void setShouldRemoveAvatar(boolean shouldRemoveAvatar) {
        this.shouldRemoveAvatar = shouldRemoveAvatar;
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

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
}
