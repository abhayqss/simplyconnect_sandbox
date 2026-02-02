package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent an event author. Usually it's a patient or employee submitting the event.
 */
@ApiModel(description = "This DTO is intended to represent event author. Usually it's a patient or employee submitting the event.")
public class EventAuthorDto {

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("roleId")
    private Long roleId = null;
/*
    @JsonProperty("organization")
    private String organization = null;*/


    /**
     * First name
     *
     * @return firstName
     */
    @NotNull
    @Size(min = 2, max = 128)
    @ApiModelProperty(example = "Donald", required = true, value = "First name")
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
    @NotNull
    @Size(min = 2, max = 128)
    @ApiModelProperty(example = "Duck", required = true, value = "Last name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotNull
    @Min(1)
    @ApiModelProperty(required = true, value = "Care Team Role ID", example = "2")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
/*
    @NotNull
    @Size(max = 128)
    @ApiModelProperty(required = true, value = "Organization")
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }*/

}
