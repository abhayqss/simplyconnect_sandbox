package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent resident's (patient's) personal data
 */
@ApiModel(description = "This DTO is intended to represent Consana-specific resident's (patient's) personal data")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-12T13:31:29.120+03:00")
public class ConsanaResidentDto extends ResidentDto {

    @JsonProperty("isActive")
    private boolean isActive;

    public boolean getIsActive() {
        return isActive;
    }

    /**
     * Is resident active
     *
     * @return isActive
     */
    @ApiModelProperty(example = "false", value = "is resident active")
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
