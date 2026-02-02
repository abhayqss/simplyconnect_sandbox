package com.scnsoft.eldermark.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This dto is is intended to represent a resolve Consana Xref patient identifier request
 */
@ApiModel(description = "This dto is is intended to represent a resolve Consana Xref patient identifier request")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-11-13T18:49:56.390+03:00")
public class ConsanaXrefPatientIdDto {

    @JsonProperty("identifier")
    private String identifier = null;

    @JsonProperty("organizationOID")
    private String organizationOID = null;

    @JsonProperty("communityOID")
    private String communityOID = null;

    public ConsanaXrefPatientIdDto() {
    }

    public ConsanaXrefPatientIdDto(String identifier, String organizationOID, String communityOID) {
        this.identifier = identifier;
        this.organizationOID = organizationOID;
        this.communityOID = communityOID;
    }

    /**
    * ITI patient identifier
    *
    * @return identifier
    */
   
    @ApiModelProperty(example = "S12345", value = "ITI patient identifier")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
    * ITI patient identifier
    *
    * @return organizationOID
    */
   
    @ApiModelProperty(example = "1.2.345677888", value = "ITI patient identifier")
    public String getOrganizationOID() {
        return organizationOID;
    }

    public void setOrganizationOID(String organizationOID) {
        this.organizationOID = organizationOID;
    }

    /**
    * ITI patient identifier
    *
    * @return communityOID
    */
   
    @ApiModelProperty(example = "1.2.34567781234.6.7.8.9", value = "ITI patient identifier")
    public String getCommunityOID() {
        return communityOID;
    }

    public void setCommunityOID(String communityOID) {
        this.communityOID = communityOID;
    }

}
