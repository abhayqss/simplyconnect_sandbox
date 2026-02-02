package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import com.scnsoft.eldermark.entity.community.Community_;

public class CareTeamMemberListItemDto {
    private Long id;
    private Long avatarId;

    private Long employeeCommunityId;

    @EntitySort(joined = {CareTeamMember_.EMPLOYEE, Employee_.COMMUNITY, Community_.NAME})
    private String communityName;

    private Long organizationId;

    @EntitySort(joined = {CareTeamMember_.EMPLOYEE, Employee_.ORGANIZATION, Organization_.NAME})
    private String organizationName;

    private String description;
    private String email;
    private String phone;
    private String roleName;


    @DefaultSort
    @EntitySort.List(
            {
                    @EntitySort(joined = {CareTeamMember_.EMPLOYEE, Employee_.FIRST_NAME}),
                    @EntitySort(joined = {CareTeamMember_.EMPLOYEE, Employee_.LAST_NAME})
            }
    )
    private String contactName;
    private boolean canViewContact;
    private boolean canEdit;
    private boolean canDelete;
    private boolean isActive;
    private Long employeeId;
    private Long clientId;
    private boolean canViewClient;

    private boolean isConversationAllowed;
    private String conversationSid;

    private boolean isVideoCallAllowed;

    private boolean isPrimaryContact;

    private boolean isOnHold;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Long getEmployeeCommunityId() {
        return employeeCommunityId;
    }

    public void setEmployeeCommunityId(Long employeeCommunityId) {
        this.employeeCommunityId = employeeCommunityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean getCanViewContact() {
        return canViewContact;
    }

    public void setCanViewContact(boolean canViewContact) {
        this.canViewContact = canViewContact;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public boolean getCanViewClient() {
        return canViewClient;
    }

    public void setCanViewClient(boolean canViewClient) {
        this.canViewClient = canViewClient;
    }

    public boolean getIsConversationAllowed() {
        return isConversationAllowed;
    }

    public void setIsConversationAllowed(boolean conversationAllowed) {
        isConversationAllowed = conversationAllowed;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public boolean getIsVideoCallAllowed() {
        return isVideoCallAllowed;
    }

    public void setIsVideoCallAllowed(boolean videoCallAllowed) {
        isVideoCallAllowed = videoCallAllowed;
    }

    public boolean getIsPrimaryContact() {
        return isPrimaryContact;
    }

    public void setIsPrimaryContact(boolean isPrimaryContact) {
        this.isPrimaryContact = isPrimaryContact;
    }

    public boolean getIsOnHold() {
        return isOnHold;
    }

    public void setIsOnHold(boolean onHold) {
        isOnHold = onHold;
    }
}
