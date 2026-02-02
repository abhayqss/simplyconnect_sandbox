package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent plan of care
 */
@ApiModel(description = "This DTO is intended to represent plan of care")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T18:43:14.547+03:00")
public class PlanOfCareInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("plannedActivity")
    private String plannedActivity = null;

    @JsonProperty("activityDate")
    private Long activityDate = null;

    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * activity
     *
     * @return plannedActivity
     */
    @ApiModelProperty(value = "activity")
    public String getPlannedActivity() {
        return plannedActivity;
    }

    public void setPlannedActivity(String plannedActivity) {
        this.plannedActivity = plannedActivity;
    }

    /**
     * activity planned date
     *
     * @return activityDate
     */
    @ApiModelProperty(value = "activity planned date")
    public Long getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Long activityDate) {
        this.activityDate = activityDate;
    }
}

