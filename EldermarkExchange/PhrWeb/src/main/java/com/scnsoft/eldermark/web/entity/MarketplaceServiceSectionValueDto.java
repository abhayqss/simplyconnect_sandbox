package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent value in service section
 */
@ApiModel(description = "This DTO is intended to represent value in service section")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-14T12:47:41.150+03:00")
public class MarketplaceServiceSectionValueDto {

    @JsonProperty("name")
    private String name = null;


    @ApiModelProperty(value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
