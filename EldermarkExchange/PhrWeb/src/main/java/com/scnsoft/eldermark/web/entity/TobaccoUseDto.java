package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent tobacco use details
 */
@ApiModel(description = "This DTO is intended to represent tobacco use details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T22:30:56.458+03:00")
public class TobaccoUseDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("startDate")
    private Long startDate = null;

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



    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}
