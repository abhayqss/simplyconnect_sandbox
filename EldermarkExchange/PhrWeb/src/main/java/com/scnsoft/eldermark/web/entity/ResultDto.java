package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent result details
 */
@ApiModel(description = "This DTO is intended to represent result details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T11:32:45.982+03:00")
public class ResultDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("resultDate")
    private Long resultDate = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("value")
    private String value = null;

    @JsonProperty("interpretations")
    private String interpretations = null;

    @JsonProperty("referenceRanges")
    private String referenceRanges = null;

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
     * result date
     *
     * @return resultDate
     */

    @ApiModelProperty(example = "1326862800000", value = "result date")
    public Long getResultDate() {
        return resultDate;
    }

    public void setResultDate(Long resultDate) {
        this.resultDate = resultDate;
    }

    /**
     * result status
     *
     * @return status
     */

    @ApiModelProperty(example = "Active", value = "result status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * result type
     *
     * @return type
     */

    @ApiModelProperty(example = "HGB", value = "result type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * result value
     *
     * @return value
     */

    @ApiModelProperty(example = "13, g/dl", value = "result value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * result interpretations
     *
     * @return interpretations
     */

    @ApiModelProperty(example = "Normal", value = "result interpretations")
    public String getInterpretations() {
        return interpretations;
    }

    public void setInterpretations(String interpretations) {
        this.interpretations = interpretations;
    }

    /**
     * result reference ranges
     *
     * @return referenceRanges
     */

    @ApiModelProperty(example = "m 13 18 g/dl; F 12 16 g/dl;", value = "result reference ranges")
    public String getReferenceRanges() {
        return referenceRanges;
    }

    public void setReferenceRanges(String referenceRanges) {
        this.referenceRanges = referenceRanges;
    }



    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}
