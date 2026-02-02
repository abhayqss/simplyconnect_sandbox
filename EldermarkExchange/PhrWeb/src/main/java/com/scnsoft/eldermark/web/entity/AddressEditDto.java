package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.dozer.Mapping;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent editable postal addresses (personal)
 */
@ApiModel(description = "This DTO is intended to represent editable postal addresses (personal)")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-23T14:03:48.150+03:00")
public class AddressEditDto {

    @JsonProperty("postalCode")
    private String postalCode = null;

    @Mapping("this")    // Dozer ignore
    @JsonProperty("state")
    private String state = null;

    @JsonProperty("city")
    private String city = null;

    @JsonProperty("streetAddress")
    private String streetAddress = null;


    /**
     * USA zip code (5 digits)
     *
     * @return postalCode
     */
    @ApiModelProperty(example = "55343", value = "USA zip code (5 digits)", required = true)
    @NotNull
    @Pattern(regexp = "^\\d{5}$")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @ApiModelProperty(required = true)
    @NotNull
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @ApiModelProperty(example = "Minnetonka", required = true)
    @NotNull
    @Size(max = 128)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @ApiModelProperty(example = "38 Blueside Road Nomes", required = true)
    @NotNull
    @Size(max = 255)
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public boolean hasContent() {
        return StringUtils.isNotBlank(getCity()) || StringUtils.isNotBlank(getState()) || StringUtils.isNotBlank(getPostalCode()) || StringUtils.isNotBlank(getStreetAddress());
    }

}

