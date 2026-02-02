package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T13:55:39.161+03:00")
public class OrgDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;


    /**
     * organization id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(value = "organization id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * organization name
     *
     * @return name
     */
    @ApiModelProperty(value = "organization name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
