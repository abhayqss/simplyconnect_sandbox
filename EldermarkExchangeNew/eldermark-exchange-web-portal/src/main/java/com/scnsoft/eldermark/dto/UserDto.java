package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.dto.employee.EmployeeAssociatedClientDto;
import com.scnsoft.eldermark.dto.notifications.inapp.InAppNotificationDto;

import java.util.List;

public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String fullName;

    private Long organizationId;

    private String organizationName;

    private Long communityId;

    private String communityName;

    private String status;

    private String token;

    private String roleTitle;

    private String roleName;

    private Long lastLoginDate;

    private List<InAppNotificationDto> notifications;

    private String email;

    private Long avatarId;

    private String conversationAccessToken;

    private boolean areConversationsEnabled;

    private String serviceConversationSid;

    private boolean areVideoCallsEnabled;

    private boolean isDocuTrackEnabled;

    private boolean isPaperlessHealthcareEnabled;

    private List<EmployeeAssociatedClientDto> associatedClients;

    public UserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Long lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public List<InAppNotificationDto> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<InAppNotificationDto> notifications) {
        this.notifications = notifications;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getConversationAccessToken() {
        return conversationAccessToken;
    }

    public void setConversationAccessToken(String conversationAccessToken) {
        this.conversationAccessToken = conversationAccessToken;
    }

    public boolean getAreConversationsEnabled() {
        return areConversationsEnabled;
    }

    public void setAreConversationsEnabled(boolean areConversationsEnabled) {
        this.areConversationsEnabled = areConversationsEnabled;
    }

    public String getServiceConversationSid() {
        return serviceConversationSid;
    }

    public void setServiceConversationSid(String serviceConversationSid) {
        this.serviceConversationSid = serviceConversationSid;
    }

    public boolean getAreVideoCallsEnabled() {
        return areVideoCallsEnabled;
    }

    public void setAreVideoCallsEnabled(boolean areVideoCallsEnabled) {
        this.areVideoCallsEnabled = areVideoCallsEnabled;
    }

    public boolean getIsDocuTrackEnabled() {
        return isDocuTrackEnabled;
    }

    public void setIsDocuTrackEnabled(boolean docuTrackEnabled) {
        isDocuTrackEnabled = docuTrackEnabled;
    }

    public boolean getIsPaperlessHealthcareEnabled() {
        return isPaperlessHealthcareEnabled;
    }

    public void setIsPaperlessHealthcareEnabled(boolean paperlessHealthcareEnabled) {
        isPaperlessHealthcareEnabled = paperlessHealthcareEnabled;
    }

    public List<EmployeeAssociatedClientDto> getAssociatedClients() {
        return associatedClients;
    }

    public void setAssociatedClients(List<EmployeeAssociatedClientDto> associatedClients) {
        this.associatedClients = associatedClients;
    }
}
