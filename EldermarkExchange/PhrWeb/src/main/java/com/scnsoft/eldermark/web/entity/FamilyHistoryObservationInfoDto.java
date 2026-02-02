package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.Date;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-07T16:20:18.041+03:00")
public class FamilyHistoryObservationInfoDto {

    @JsonProperty("problemType")
    private String problemType = null;

    @JsonProperty("problemName")
    private String problemName = null;

    @JsonProperty("date")
    private Date date = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("observedAge")
    private String observedAge = null;

    @JsonProperty("isObservedDead")
    private Boolean isObservedDead = null;


    /**
    * Problem type
    *
    * @return problemType
    */
   
    @ApiModelProperty(example = "Diagnosis", value = "Problem type")
    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    /**
    * Problem name
    *
    * @return problemName
    */
   
    @ApiModelProperty(example = "Diabet", value = "Problem name")
    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    /**
    * Observation date
    *
    * @return date
    */
   
    @ApiModelProperty(example = "1326862800000", value = "Observation date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
    * Problem status
    *
    * @return status
    */
   
    @ApiModelProperty(example = "Completed", value = "Problem status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
    * Age when observation happened
    *
    * @return observedAge
    */
   
    @ApiModelProperty(example = "Completed", value = "Age when observation happened")
    public String getObservedAge() {
        return observedAge;
    }

    public void setObservedAge(String observedAge) {
        this.observedAge = observedAge;
    }

    /**
    * If observation subject is dead
    *
    * @return isObservedDead
    */
   
    @ApiModelProperty(value = "If observation subject is dead")
    public Boolean IsObservedDead() {
        return isObservedDead;
    }

    public void setIsObservedDead(Boolean isObservedDead) {
        this.isObservedDead = isObservedDead;
    }

}
