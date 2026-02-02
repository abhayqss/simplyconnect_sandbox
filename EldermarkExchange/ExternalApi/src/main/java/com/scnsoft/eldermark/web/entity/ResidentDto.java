package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent resident's (patient's) personal data
 */
@ApiModel(description = "This DTO is intended to represent resident's (patient's) personal data")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-12T13:31:29.120+03:00")
public class ResidentDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("middleName")
    private String middleName = null;

    @JsonProperty("phone")
    private String phone = null;

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("orgId")
    private Long orgId = null;

    @JsonProperty("orgName")
    private String orgName = null;

    @JsonProperty("communityId")
    private Long communityId = null;

    @JsonProperty("communityName")
    private String communityName = null;


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
     * Middle name. Nullable
     *
     * @return middleName
     */
    @Size(max = 128)
    @ApiModelProperty(example = "Fauntleroy", value = "Middle name. Nullable")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Phone number. Nullable
     *
     * @return phone
     */
    @Size(max = 150)
    @ApiModelProperty(example = "6458765432", value = "Phone number. Nullable")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Email address. Nullable
     *
     * @return email
     */
    @Size(max = 150)
    @ApiModelProperty(example = "dduck@disney.com", value = "Email address. Nullable")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    /**
     * community id
     * minimum: 1
     *
     * @return communityId
     */
    @Min(1)
    @ApiModelProperty(value = "community id")
    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
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

}
