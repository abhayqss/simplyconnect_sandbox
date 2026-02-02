package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent an Advance Directive in LISTING
 */
@ApiModel(description = "This DTO is intended to represent an Advance Directive in LISTING")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T15:56:03.515-03:00")
public class AdvanceDirectiveInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("type")
    private String advanceDirectiveType = null;

    @JsonProperty("effectiveTimeLow")
    private Long effectiveTimeLow = null;

    @JsonProperty("effectiveTimeHigh")
    private Long effectiveTimeHigh = null;

    @JsonProperty("status")
    private String status = null;


    
   
    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
    * Advanced directive type
    *
    * @return advanceDirectiveType
    */
   
    @ApiModelProperty(example = "Resuscitation", value = "Advanced directive type")
    public String getAdvanceDirectiveType() {
        return advanceDirectiveType;
    }

    public void setAdvanceDirectiveType(String advanceDirectiveType) {
        this.advanceDirectiveType = advanceDirectiveType;
    }

    /**
    * effective time low
    *
    * @return effectiveTimeLow
    */
   
    @ApiModelProperty(example = "1336338000000", value = "effective time low")
    public Long getEffectiveTimeLow() {
        return effectiveTimeLow;
    }

    public void setEffectiveTimeLow(Long effectiveTimeLow) {
        this.effectiveTimeLow = effectiveTimeLow;
    }

    /**
    * effective time high
    *
    * @return effectiveTimeHigh
    */
   
    @ApiModelProperty(example = "1336338000000", value = "effective time high")
    public Long getEffectiveTimeHigh() {
        return effectiveTimeHigh;
    }

    public void setEffectiveTimeHigh(Long effectiveTimeHigh) {
        this.effectiveTimeHigh = effectiveTimeHigh;
    }

    /**
    * status
    *
    * @return status
    */
   
    @ApiModelProperty(example = "Active", value = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
