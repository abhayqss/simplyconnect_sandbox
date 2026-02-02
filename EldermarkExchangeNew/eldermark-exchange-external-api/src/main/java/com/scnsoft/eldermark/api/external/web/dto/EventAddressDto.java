package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.api.shared.dto.KeyValueDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent editable postal addresses (for events)
 */
@ApiModel(description = "This DTO is intended to represent editable postal addresses (for events)")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class EventAddressDto {

    @JsonProperty("city")
    private String city = null;

    @JsonProperty("zip")
    private String zip = null;

    @JsonProperty("state")
    private KeyValueDto state = null;

    @JsonProperty("street")
    private String street = null;

    @JsonProperty("displayAddress")
    private String displayAddress = null;


    @NotNull
    @Size(max = 128)
    @ApiModelProperty(example = "Minnetonka", required = true)
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
    @NotNull
    @Pattern(regexp = "^\\d{5}$")
    @ApiModelProperty(example = "55343", required = true, value = "USA zip code (5 digits)")
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    public KeyValueDto getState() {
        return state;
    }

    public void setState(KeyValueDto state) {
        this.state = state;
    }

    @NotNull
    @Size(max = 255)
    @ApiModelProperty(example = "38 Blueside Road Nomes", required = true)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Full address (computed attribute)
     *
     * @return displayAddress
     */

    @ApiModelProperty(example = "38 Blueside Road Nomes Minnetonka Illinois (IL) 55343", value = "Full address (computed attribute)")
    public String getDisplayAddress() {
        return displayAddress;
    }

    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }

}

