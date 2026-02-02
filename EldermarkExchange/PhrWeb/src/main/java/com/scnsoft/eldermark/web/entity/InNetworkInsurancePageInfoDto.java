package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent page number of insurance
 */
@ApiModel(description = "This DTO is intended to represent page number of insurance")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-22T10:51:40.688+03:00")
public class InNetworkInsurancePageInfoDto {

    @JsonProperty("insuranceId")
    private Long insuranceId = null;

    @JsonProperty("page")
    private Integer page = null;


    @ApiModelProperty(value = "")
    public Long getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Long insuranceId) {
        this.insuranceId = insuranceId;
    }

    @ApiModelProperty(value = "")
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

}

