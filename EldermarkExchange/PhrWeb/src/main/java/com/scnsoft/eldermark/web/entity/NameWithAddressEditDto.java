package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent contact info.
 */
@ApiModel(description = "This DTO is intended to represent contact info.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-06T14:22:46.197+03:00")
public class NameWithAddressEditDto {

    @JsonProperty("address")
    private EventAddressEditDto address = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("includeAddress")
    private Boolean includeAddress = null;

    @JsonProperty("phone")
    private String phone = null;


    @Valid
    @ApiModelProperty
    public EventAddressEditDto getAddress() {
        return address;
    }

    public void setAddress(EventAddressEditDto address) {
        this.address = address;
    }

    /**
     * First name
     *
     * @return firstName
     */
    @ApiModelProperty(example = "Donald", required = true, value = "First name")
    @NotNull
    @Size(min = 2, max = 128)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Last name
     *
     * @return lastName
     */
    @ApiModelProperty(example = "Duck", required = true, value = "Last name")
    @NotNull
    @Size(min = 2, max = 128)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ApiModelProperty(required = true, example = "true")
    @NotNull
    public boolean isIncludeAddress() {
        return includeAddress;
    }

    public void setIncludeAddress(boolean includeAddress) {
        this.includeAddress = includeAddress;
    }

    /**
     * Phone number. Nullable. Moreover `responsible` (registered nurse) doesn't have any associated phone, so you can omit this field for `responsible`.
     *
     * @return phone
     */
    @ApiModelProperty(example = "6458765432", value = "Phone number. Nullable. Moreover `responsible` (registered nurse) doesn't have any associated phone, so you can omit this field for `responsible`.")
    @Size(max = 16)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // custom validation
/*
    @AssertTrue
    private boolean isAddressIncludedWhenIncludeAddressIsTrue() {
        return !Boolean.TRUE.equals(includeAddress) || address != null;
    }
*/
}

