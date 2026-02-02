package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent Hospital / Clinic contact info (Details of Treating Hospital).
 */
@ApiModel(description = "This DTO is intended to represent Hospital / Clinic contact info (Details of Treating Hospital).")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class HospitalEditDto {

    @JsonProperty("address")
    private EventAddressDto address = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("phone")
    private String phone = null;


    @ApiModelProperty
    public EventAddressDto getAddress() {
        return address;
    }

    public void setAddress(EventAddressDto address) {
        this.address = address;
    }

    /**
     * Hospital / Clinic name
     *
     * @return name
     */
    @NotNull
    @Size(max = 255)
    @ApiModelProperty(required = true, value = "Hospital / Clinic name", example = "Princeton Plainsboro")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(max = 16)
    @ApiModelProperty(example = "6458765432")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

