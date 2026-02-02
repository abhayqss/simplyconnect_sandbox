package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T13:55:39.161+03:00")
public class CommunityDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("orgId")
    private Long orgId = null;

    @JsonProperty("orgName")
    private String orgName = null;


    /**
     * community id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(value = "community id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * community name
     *
     * @return name
     */
    @ApiModelProperty(value = "community name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * organization id
     * minimum: 1
     *
     * @return orgId
     */
    @Min(1)
    @ApiModelProperty(value = "organization id")
    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    /**
     * organization name
     *
     * @return orgName
     */
    @ApiModelProperty(value = "organization name")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

}
