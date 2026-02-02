package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * DTO to represent organization
 */
@ApiModel(description = "DTO to represent organization")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T08:01:57.882-03:00")
public class RepresentedOrganizationDto {

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("providerType")
    private String providerType = null;

    @JsonProperty("nationalProviderId")
    private String nationalProviderId = null;

    @JsonProperty("adress")
    private String adress = null;


    /**
    * name
    *
    * @return name
    */
   
    @ApiModelProperty(example = "Happy Health", value = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
    * Provider type
    *
    * @return providerType
    */
   
    @ApiModelProperty(example = "Healthcare Provider", value = "Provider type")
    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    /**
    * National provider ID
    *
    * @return nationalProviderId
    */
   
    @ApiModelProperty(example = "34343434343434", value = "National provider ID")
    public String getNationalProviderId() {
        return nationalProviderId;
    }

    public void setNationalProviderId(String nationalProviderId) {
        this.nationalProviderId = nationalProviderId;
    }

    /**
    * Address
    *
    * @return adress
    */
   
    @ApiModelProperty(example = "484 Bluff Street, Rapid City, South Dakota, 57701", value = "Address")
    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

}
