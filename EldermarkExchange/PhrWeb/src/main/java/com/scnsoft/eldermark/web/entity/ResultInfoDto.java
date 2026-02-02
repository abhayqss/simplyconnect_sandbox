package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent result in list
 */
@ApiModel(description = "This DTO is intended to represent result in list")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T11:32:45.982+03:00")
public class ResultInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("resultDate")
    private Long resultDate = null;




    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * result type
     *
     * @return type
     */

    @ApiModelProperty(example = "HBG", value = "result type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

}
