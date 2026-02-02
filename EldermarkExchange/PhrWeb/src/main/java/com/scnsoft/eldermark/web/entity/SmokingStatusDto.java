package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent smoking status observation details
 */
@ApiModel(description = "This DTO is intended to represent smoking status observation details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T21:16:09.800+03:00")
public class SmokingStatusDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("smokingStatus")
    private String smokingStatus = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("startDate")
    private Long startDate = null;

    @JsonProperty("endDate")
    private Long endDate = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;




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

    /**
     * Start Date
     *
     * @return startDate
     */

    @ApiModelProperty(example = "1326862800000", value = "Start Date")
    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    /**
     * End Date
     *
     * @return endDate
     */

    @ApiModelProperty(example = "1326862800000", value = "End Date")
    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }



    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}
