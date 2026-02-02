package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Pattern;

/**
 * This DTO is intended to represent all fields in personal profile.
 */
@ApiModel(description = "This DTO is intended to represent all fields in personal profile.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-23T14:12:02.181+03:00")
public class PersonalProfileDto {

    @JsonProperty("phone")
    private String phone = null;

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("secondaryPhone")
    private String secondaryPhone = null;

    @JsonProperty("secondaryEmail")
    private String secondaryEmail = null;

    @JsonProperty("address")
    private AddressDto address = null;

    @JsonProperty("lastFourDigitsOfSsn")
    private String lastFourDigitsOfSsn = null;


    /**
     * Primary phone. Non editable.
     *
     * @return phone
     */
    @ApiModelProperty(example = "6458765432", value = "Primary phone. Non editable.")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Primary email. Non editable.
     *
     * @return email
     */
    @ApiModelProperty(example = "cpatnode@test.com", value = "Primary email. Non editable.")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Secondary phone.
     *
     * @return secondaryPhone
     */
    @ApiModelProperty(example = "6452345678", value = "Secondary phone.")
    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    /**
     * Secondary email.
     *
     * @return secondaryEmail
     */
    @ApiModelProperty(example = "public.cpatnode@test.com", value = "Secondary email.")
    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    @ApiModelProperty
    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    /**
     * Last 4 digits of social security number (SSN). May be null if not specified by inviter.
     *
     * @return lastFourDigitsOfSsn
     */
    @ApiModelProperty(example = "6789", value = "Last 4 digits of social security number (SSN). May be null if not specified by inviter.")
    @Pattern(regexp = "^(?!0000)\\d{4}$")
    public String getLastFourDigitsOfSsn() {
        return lastFourDigitsOfSsn;
    }

    public void setLastFourDigitsOfSsn(String lastFourDigitsOfSsn) {
        this.lastFourDigitsOfSsn = lastFourDigitsOfSsn;
    }

}

