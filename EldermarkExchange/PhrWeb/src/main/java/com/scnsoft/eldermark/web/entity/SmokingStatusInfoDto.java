package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent social history - smoking status observation in list
 */
@ApiModel(description = "This DTO is intended to represent social history - smoking status observation in list")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T21:16:09.800+03:00")
public class SmokingStatusInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("smokingStatus")
    private String smokingStatus = null;

    @JsonProperty("effectiveTimeLow")
    private Long effectiveTimeLow = null;

    @JsonProperty("effectiveTimeHigh")
    private Long effectiveTimeHigh = null;




    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * smoking status
     *
     * @return smokingStatus
     */

    @ApiModelProperty(example = "Current every day smoker", value = "smoking status")
    public String getSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
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

    /**
     * Effective Time High
     *
     * @return effectiveTimeHigh
     */

    @ApiModelProperty(example = "1326862800000", value = "Effective Time High")
    public Long getEffectiveTimeHigh() {
        return effectiveTimeHigh;
    }

    public void setEffectiveTimeHigh(Long effectiveTimeHigh) {
        this.effectiveTimeHigh = effectiveTimeHigh;
    }

}
