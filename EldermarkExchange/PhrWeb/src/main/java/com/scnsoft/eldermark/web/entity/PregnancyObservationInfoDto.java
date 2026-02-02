package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent social history - pregnancy observation in list
 */
@ApiModel(description = "This DTO is intended to represent social history - pregnancy observation in list")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T21:16:09.800+03:00")
public class PregnancyObservationInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("effectiveTimeLow")
    private Long effectiveTimeLow = null;




    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Encounter name
     *
     * @return name
     */

    @ApiModelProperty(example = "Office visit", value = "Encounter name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Effective Time Low
     *
     * @return effectiveTimeLow
     */

    @ApiModelProperty(example = "1326862800000", value = "Effective Time Low")
    public Long getEffectiveTimeLow() {
        return effectiveTimeLow;
    }

    public void setEffectiveTimeLow(Long effectiveTimeLow) {
        this.effectiveTimeLow = effectiveTimeLow;
    }

}
