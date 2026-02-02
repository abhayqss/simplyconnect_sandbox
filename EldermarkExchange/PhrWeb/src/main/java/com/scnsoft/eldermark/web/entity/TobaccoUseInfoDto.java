package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent social history - tobacco use in list
 */
@ApiModel(description = "This DTO is intended to represent social history - tobacco use in list")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T22:30:56.458+03:00")
public class TobaccoUseInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("effectiveTime")
    private Long effectiveTime = null;




    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * tobacco use type
     *
     * @return type
     */

    @ApiModelProperty(example = "Chews tobacco", value = "tobacco use type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Effective Time
     *
     * @return effectiveTime
     */

    @ApiModelProperty(example = "1326862800000", value = "Effective Time")
    public Long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

}
