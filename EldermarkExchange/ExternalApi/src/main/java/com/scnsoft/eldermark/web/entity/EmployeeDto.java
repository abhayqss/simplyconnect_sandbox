package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent a contact or a person submitting the event (aka Event Author, see Event Essentials).
 */
@ApiModel(description = "This DTO is intended to represent a contact or a person submitting the event (aka Event Author, see Event Essentials).")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-01T15:06:46.623+03:00")
public class EmployeeDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("displayName")
    private String displayName = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("phone")
    private String phone = null;

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("role")
    private String role = null;

    @JsonProperty("roleId")
    private Long roleId = null;

    @JsonProperty("orgId")
    private Long orgId = null;

    @JsonProperty("communityId")
    private Long communityId = null;


    /**
     * employee id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(value = "employee id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Employee's display name
     *
     * @return displayName
     */
    @Size(max = 256)
    @ApiModelProperty(example = "Donald Duck", value = "Employee's display name")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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


    @ApiModelProperty
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Min(1)
    @ApiModelProperty
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

}
