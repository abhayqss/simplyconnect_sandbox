package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent contact info.
 */
@ApiModel(description = "This DTO is intended to represent contact info.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class NameWithAddressEditDto {

    @JsonProperty("address")
    private EventAddressDto address = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("phone")
    private String phone = null;


    @Valid
    @ApiModelProperty
    public EventAddressDto getAddress() {
        return address;
    }

    public void setAddress(EventAddressDto address) {
        this.address = address;
    }

    /**
     * First name
     *
     * @return firstName
     */
    @NotNull
    @Size(min = 2, max = 128)
    @ApiModelProperty(example = "Donald", required = true, value = "First name")
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
    @NotNull
    @Size(min = 2, max = 128)
    @ApiModelProperty(example = "Duck", required = true, value = "Last name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Phone number. Nullable. Moreover `responsible` (registered nurse) doesn't have any associated phone, so you can omit this field for `responsible`.
     *
     * @return phone
     */
    @Size(max = 16)
    @ApiModelProperty(example = "6458765432", value = "Phone number. Nullable. Moreover `responsible` (registered nurse) doesn't have any associated phone, so you can omit this field for `responsible`.")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

