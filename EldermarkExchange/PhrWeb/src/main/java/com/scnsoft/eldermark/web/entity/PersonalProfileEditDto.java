package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.shared.validation.Phone;
import com.scnsoft.eldermark.shared.validation.Ssn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Email;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * This DTO is intended to represent editable fields in personal profile.
 */
@ApiModel(description = "This DTO is intended to represent editable fields in personal profile.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-23T14:12:02.181+03:00")
public class PersonalProfileEditDto {

    @Phone
    @JsonProperty("secondaryPhone")
    private String secondaryPhone = null;

    @Email
    @JsonProperty("secondaryEmail")
    private String secondaryEmail = null;

    @JsonProperty("address")
    private AddressEditDto address = null;

    @Ssn
    @JsonProperty("ssn")
    private String ssn = null;


    /**
     * Secondary phone. Leave blank to delete. Set null or omit to keep without changes.
     *
     * @return secondaryPhone
     */
    @ApiModelProperty(example = "6452345678", value = "Secondary phone. Leave blank to delete. Set null or omit to keep without changes.")
    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    /**
     * Secondary email. Leave blank to delete. Set null or omit to keep without changes.
     *
     * @return secondaryEmail
     */
    @ApiModelProperty(example = "public.cpatnode@test.com", value = "Secondary email. Leave blank to delete. Set null or omit to keep without changes.")
    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    @Valid
    @ApiModelProperty
    public AddressEditDto getAddress() {
        return address;
    }

    public void setAddress(AddressEditDto address) {
        this.address = address;
    }

    /**
     * Social security number (SSN). Editable if not specified by inviter?
     *
     * @return ssn
     */
    @ApiModelProperty(example = "123456789", value = "Social security number (SSN). Editable if not specified by inviter.")
    @Pattern(regexp = "^(?!(000|666|9))\\d{3}(?!00)\\d{2}(?!0000)\\d{4}$")
    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

}

