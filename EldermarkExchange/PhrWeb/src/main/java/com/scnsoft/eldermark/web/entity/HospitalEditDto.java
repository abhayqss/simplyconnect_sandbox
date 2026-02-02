package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent Hospital / Clinic contact info (Details of Treating Hospital).
 */
@ApiModel(description = "This DTO is intended to represent Hospital / Clinic contact info (Details of Treating Hospital).")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-09T12:09:43.945+03:00")
public class HospitalEditDto {

    @JsonProperty("address")
    private EventAddressEditDto address = null;

    @JsonProperty("includeAddress")
    private Boolean includeAddress = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("phone")
    private String phone = null;


    @ApiModelProperty
    public EventAddressEditDto getAddress() {
        return address;
    }

    public void setAddress(EventAddressEditDto address) {
        this.address = address;
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
     * Hospital / Clinic name
     *
     * @return name
     */
    @ApiModelProperty(required = true, value = "Hospital / Clinic name")
    @NotNull
    @Size(max = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(example = "6458765432")
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

