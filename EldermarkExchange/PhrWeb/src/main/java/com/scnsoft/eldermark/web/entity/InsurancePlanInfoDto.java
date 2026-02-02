package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent insurance plan
 */
@ApiModel(description = "This DTO is intended to represent insurance plan")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-26T16:08:04.225+03:00")
public class InsurancePlanInfoDto extends BaseDisplayableNamedKeyDto {

    @JsonProperty("insuranceName")
    private String insuranceName = null;

    /**
     * Insurance Name.
     */
    @ApiModelProperty(example = "Aetna", value = "Insurance Name.")
    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

}
