package com.scnsoft.eldermark.mobile.dto.client;

import com.scnsoft.eldermark.entity.client.ClientPrimaryContactType;
import io.swagger.annotations.ApiModelProperty;

public class ClientPrimaryContactDto {
    @ApiModelProperty(readOnly = true)
    private Long careTeamMemberId;
    @ApiModelProperty(readOnly = true)
    private ClientPrimaryContactType type;
    @ApiModelProperty(readOnly = true)
    private String firstName;
    @ApiModelProperty(readOnly = true)
    private String lastName;
    @ApiModelProperty(readOnly = true)
    private String roleName;
    @ApiModelProperty(readOnly = true)
    private String roleTitle;

    public Long getCareTeamMemberId() {
        return careTeamMemberId;
    }

    public void setCareTeamMemberId(Long careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    public ClientPrimaryContactType getType() {
        return type;
    }

    public void setType(ClientPrimaryContactType type) {
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }
}
