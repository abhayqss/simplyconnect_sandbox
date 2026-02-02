package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent editable postal addresses (for events)
 */
@ApiModel(description = "This DTO is intended to represent postal addresses")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-09T12:09:43.945+03:00")
public class EventAddressEditDto {

    @JsonProperty("city")
    private String city = null;

    @JsonProperty("zip")
    private String zip = null;

    @JsonProperty("state")
    private KeyValueDto state = null;

    @JsonProperty("street")
    private String street = null;


    @ApiModelProperty(example = "Minnetonka", required = true)
    @NotNull
    @Size(max = 128)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * USA zip code (5 digits)
     *
     * @return zip
     */
    @ApiModelProperty(example = "55343", required = true, value = "USA zip code (5 digits)")
    @NotNull
    @Pattern(regexp = "^\\d{5}$")
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @ApiModelProperty(required = true)
    @NotNull
    public KeyValueDto getState() {
        return state;
    }

    public void setState(KeyValueDto state) {
        this.state = state;
    }

    @ApiModelProperty(example = "38 Blueside Road Nomes", required = true)
    @NotNull
    @Size(max = 255)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

}

