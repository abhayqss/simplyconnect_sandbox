package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * DTO to represent encounter additional info
 */
@ApiModel(description = "DTO to represent encounter additional info")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T08:01:57.882-03:00")
public class EncounterAdditionalInfoDto {

    @JsonProperty("location")
    private String location = null;

    @JsonProperty("dateRecorded")
    private Long dateRecorded = null;

    @JsonProperty("recordedBy")
    private String recordedBy = null;

    @JsonProperty("comment")
    private String comment = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;


    /**
    * Location
    *
    * @return location
    */
   
    @ApiModelProperty(example = "Allina Health Shakopee Clinic", value = "Location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
    * Date Recorded
    *
    * @return dateRecorded
    */
   
    @ApiModelProperty(example = "1336338000000", value = "Date Recorded")
    public Long getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(Long dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    /**
    * Recorded by
    *
    * @return recordedBy
    */
   
    @ApiModelProperty(example = "Nale Tyler", value = "Recorded by")
    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    /**
    * Comment
    *
    * @return comment
    */
   
    @ApiModelProperty(example = "Comments if any", value = "Comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    
   
    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}
