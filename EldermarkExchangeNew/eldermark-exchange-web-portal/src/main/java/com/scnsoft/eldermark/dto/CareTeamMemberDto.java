package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.beans.security.projection.dto.CareTeamSecurityFieldsAware;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class CareTeamMemberDto implements CareTeamSecurityFieldsAware {

    private Long id;

    private Long clientId;

    private Long communityId;

    @Size(max = 256)
    private String description;

    @NotNull
    private Long employeeId;

    private String employeeName;

    private String employeeOrganizationName;

    @NotEmpty
    private List<NotificationsPreferencesDto> notificationsPreferences;

    @NotNull
    private Long roleId;

    private String roleName;

    private boolean canChangeRole;

    private Boolean includeInFaceSheet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeOrganizationName() {
        return employeeOrganizationName;
    }

    public void setEmployeeOrganizationName(String employeeOrganizationName) {
        this.employeeOrganizationName = employeeOrganizationName;
    }

    public List<NotificationsPreferencesDto> getNotificationsPreferences() {
        return notificationsPreferences;
    }

    public void setNotificationsPreferences(List<NotificationsPreferencesDto> notificationsPreferences) {
        this.notificationsPreferences = notificationsPreferences;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean getCanChangeRole() {
        return canChangeRole;
    }

    public void setCanChangeRole(boolean canChangeRole) {
        this.canChangeRole = canChangeRole;
    }

    public Boolean getIncludeInFaceSheet() {
        return includeInFaceSheet;
    }

    public void setIncludeInFaceSheet(Boolean includeInFaceSheet) {
        this.includeInFaceSheet = includeInFaceSheet;
    }

    @Override
    public Long getCareTeamRoleId() {
        return roleId;
    }
}
