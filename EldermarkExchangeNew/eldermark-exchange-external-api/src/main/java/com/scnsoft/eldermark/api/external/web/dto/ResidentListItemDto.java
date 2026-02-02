package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-12T13:31:29.120+03:00")
public class ResidentListItemDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("communityName")
    private String communityName = null;

    @JsonProperty("orgName")
    private String orgName = null;


    /**
     * resident id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(value = "resident id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * First name
     *
     * @return firstName
     */
    @Size(max = 128)
    @ApiModelProperty(example = "Donald", value = "First name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Last name
     *
     * @return lastName
     */
    @Size(max = 128)
    @ApiModelProperty(example = "Duck", value = "Last name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Community name
     *
     * @return communityName
     */
    @ApiModelProperty(example = "Unaffiliated", value = "Community name")
    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    /**
     * Organization name
     *
     * @return orgName
     */
    @ApiModelProperty(example = "Unaffiliated", value = "Organization name")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

}
