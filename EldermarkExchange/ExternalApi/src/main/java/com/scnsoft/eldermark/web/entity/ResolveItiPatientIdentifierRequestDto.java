package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This dto is is intended to represent a resolve iti patient identifier request
 */
@ApiModel(description = "This dto is is intended to represent a resolve iti patient identifier request")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-19T15:26:15.572+03:00")
public class ResolveItiPatientIdentifierRequestDto {

    @JsonProperty("identifier")
    private String identifier = null;

    @JsonProperty("assigningAuthority")
    private HDHierarchicDesignatorDto assigningAuthority = null;


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

    
   
    @ApiModelProperty(value = "")
    public HDHierarchicDesignatorDto getAssigningAuthority() {
        return assigningAuthority;
    }

    public void setAssigningAuthority(HDHierarchicDesignatorDto assigningAuthority) {
        this.assigningAuthority = assigningAuthority;
    }

}
