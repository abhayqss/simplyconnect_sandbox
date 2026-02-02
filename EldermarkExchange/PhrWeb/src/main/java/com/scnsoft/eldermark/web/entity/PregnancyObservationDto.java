package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent pregnancy observation details
 */
@ApiModel(description = "This DTO is intended to represent pregnancy observation details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T15:06:39.120+03:00")
public class PregnancyObservationDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("startDate")
    private Long startDate = null;

    @JsonProperty("endDate")
    private Long endDate = null;

    @JsonProperty("estimatedDeliveryDate")
    private Long estimatedDeliveryDate = null;

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
     * Pregnancy name
     *
     * @return name
     */

    @ApiModelProperty(example = "Pregnancy", value = "Pregnancy name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Pregnancy status
     *
     * @return status
     */

    @ApiModelProperty(example = "Active", value = "Pregnancy status")
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

    /**
     * Delivery Date
     *
     * @return estimatedDeliveryDate
     */

    @ApiModelProperty(example = "1326862800000", value = "Delivery Date")
    public Long getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Long estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }



    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }
}

