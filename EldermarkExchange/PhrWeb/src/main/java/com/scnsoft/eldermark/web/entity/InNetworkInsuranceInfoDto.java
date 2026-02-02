package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent insurance plan
 */
@ApiModel(description = "This DTO is intended to represent insurance plan")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-19T16:22:38.048+03:00")
public class InNetworkInsuranceInfoDto extends BaseDisplayableNamedKeyDto {

    @JsonProperty("hasPlans")
    private Boolean hasPlans = null;

    @ApiModelProperty(value = "")
    public Boolean getHasPlans() {
        return hasPlans;
    }

    public void setHasPlans(Boolean hasPlans) {
        this.hasPlans = hasPlans;
    }

}
