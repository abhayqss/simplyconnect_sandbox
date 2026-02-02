package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;

/**
 * This DTO is intended to represent vital sign observation (biometric data).
 */
@ApiModel(description = "This DTO is intended to represent vital sign observation (biometric data).")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class VitalSignObservationDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("dateTime")
    private Long dateTime = null;

    @JsonProperty("value")
    private Double value = null;


    /**
     * vital sign id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(value = "vital sign id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Date and time of the observation
     *
     * @return dateTime
     */
    @ApiModelProperty(example = "1435234244000", value = "Date and time of the observation")
    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }


    @ApiModelProperty(example = "82.0")
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}

