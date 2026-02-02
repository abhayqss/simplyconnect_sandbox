package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.Date;

/**
 * This Dto is intended to represent procedure details
 */
@ApiModel(description = "This Dto is intended to represent procedure details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T13:18:22.383+03:00")
public class ProcedureDetailsDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("identifiedDate")
    private Date identifiedDate = null;

    @JsonProperty("stoppedDate")
    private Date stoppedDate = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;


    @ApiModelProperty(example = "42", value = "")
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
     * Date Identified
     *
     * @return identifiedDate
     */

    @ApiModelProperty(example = "1530870210098", value = "Date Identified")
    public Date getIdentifiedDate() {
        return identifiedDate;
    }

    public void setIdentifiedDate(Date identifiedDate) {
        this.identifiedDate = identifiedDate;
    }

    /**
     * Date Identified
     *
     * @return stoppedDate
     */

    @ApiModelProperty(example = "1530870210100", value = "Date Identified")
    public Date getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Date stoppedDate) {
        this.stoppedDate = stoppedDate;
    }


    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}
