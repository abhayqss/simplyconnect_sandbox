package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This dto is is intended to represent HD data dype
 */
@ApiModel(description = "This dto is is intended to represent HD data dype")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-19T15:26:15.572+03:00")
public class HDHierarchicDesignatorDto {

    @JsonProperty("namespaceID")
    private String namespaceID = null;

    @JsonProperty("universalID")
    private String universalID = null;

    @JsonProperty("universalIDType")
    private String universalIDType = null;


    /**
    * Namespace Id
    *
    * @return namespaceID
    */
   
    @ApiModelProperty(example = "NS12345", value = "Namespace Id")
    public String getNamespaceID() {
        return namespaceID;
    }

    public void setNamespaceID(String namespaceID) {
        this.namespaceID = namespaceID;
    }

    /**
    * Universal Id
    *
    * @return universalID
    */
   
    @ApiModelProperty(example = "2.16.840.1.113883.1.234567.890", value = "Universal Id")
    public String getUniversalID() {
        return universalID;
    }

    public void setUniversalID(String universalID) {
        this.universalID = universalID;
    }

    /**
    * Universal Id Type
    *
    * @return universalIDType
    */
   
    @ApiModelProperty(example = "ISO", value = "Universal Id Type")
    public String getUniversalIDType() {
        return universalIDType;
    }

    public void setUniversalIDType(String universalIDType) {
        this.universalIDType = universalIDType;
    }

}
