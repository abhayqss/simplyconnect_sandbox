package com.scnsoft.eldermark.dto.client;

public class PrimaryContactDto {
    private Long careTeamMemberId;
    private String typeName;
    private String typeTitle;
    private String notificationMethodName;
    private String notificationMethodTitle;
    private String firstName;
    private String lastName;
    private boolean active;
    private String roleName;
    private String roleTitle;

    public Long getCareTeamMemberId() {
        return careTeamMemberId;
    }

    public void setCareTeamMemberId(Long careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getNotificationMethodName() {
        return notificationMethodName;
    }

    public void setNotificationMethodName(String notificationMethodName) {
        this.notificationMethodName = notificationMethodName;
    }

    public String getNotificationMethodTitle() {
        return notificationMethodTitle;
    }

    public void setNotificationMethodTitle(String notificationMethodTitle) {
        this.notificationMethodTitle = notificationMethodTitle;
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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
