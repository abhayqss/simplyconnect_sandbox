package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent encounter in LISTING
 */
@ApiModel(description = "This DTO is intended to represent encounter in LISTING")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T08:01:57.882-03:00")
public class EncounterInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String encounterName = null;

    @JsonProperty("startDate")
    private Long startDateTime = null;


    
   
    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
    * Encounter name (type)
    *
    * @return encounterName
    */
   
    @ApiModelProperty(example = "Office visit", value = "Encounter name (type)")
    public String getEncounterName() {
        return encounterName;
    }

    public void setEncounterName(String encounterName) {
        this.encounterName = encounterName;
    }

    /**
    * Start date
    *
    * @return startDateTime
    */
   
    @ApiModelProperty(example = "1336338000000", value = "Start date")
    public Long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Long startDateTime) {
        this.startDateTime = startDateTime;
    }

}
