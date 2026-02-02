package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent value in marketplace insurance section
 */
@ApiModel(description = "This DTO is intended to represent value in marketplace insurance section")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-14T12:47:41.150+03:00")
public class MarketplaceServiceInsuranceSectionValueDto {

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("plans")
    private List<String> plans = new ArrayList<String>();


    @ApiModelProperty(value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public MarketplaceServiceInsuranceSectionValueDto addPlansItem(String plansItem) {
        this.plans.add(plansItem);
        return this;
    }

    /**
     * It contains a list of values.
     * @return plans
     */
    @ApiModelProperty(value = "It contains a list of values.")
    public List<String> getPlans() {
        return plans;
    }

    public void setPlans(List<String> plans) {
        this.plans = plans;
    }

}

