package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-04T13:43:19.826+03:00")
public class InsuranceDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("displayName")
    private String displayName = null;


    /**
     * Insurance ID
     * minimum: 1
     *
     * @return id
     */
    @ApiModelProperty(value = "Insurance ID")
    @Min(1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Insurance description
     *
     * @return displayName
     */
    @ApiModelProperty(example = "Aetna Health Insurance", value = "Insurance description")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}

