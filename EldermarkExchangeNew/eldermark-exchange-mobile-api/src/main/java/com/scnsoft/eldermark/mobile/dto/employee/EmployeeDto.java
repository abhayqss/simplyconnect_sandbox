package com.scnsoft.eldermark.mobile.dto.employee;

import com.scnsoft.eldermark.dto.AddressDto;

import java.time.LocalDate;

public class EmployeeDto {

    private Long id;

    @Deprecated
    private boolean isOnline;

    private String firstName;
    private String lastName;
    private boolean isFavourite;
    private Long communityId;
    private String communityName;
    private Long organizationId;
    private String organizationName;
    private String role;
    private Long avatarId;
    private String avatarName;
    private String email;

    private AddressDto address;
    private String cellPhone;
    private String homePhone;
    private LocalDate birthDate;

    private String twilioUserSid;
    private boolean canStartConversation;
    private String conversationSid;
    private boolean canCall;
    private boolean canEdit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Deprecated
    public boolean getIsOnline() {
        return isOnline;
    }

    @Deprecated
    public void setIsOnline(boolean online) {
        isOnline = online;
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

    public boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean favourite) {
        isFavourite = favourite;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getTwilioUserSid() {
        return twilioUserSid;
    }

    public void setTwilioUserSid(String twilioUserSid) {
        this.twilioUserSid = twilioUserSid;
    }

    public boolean getCanStartConversation() {
        return canStartConversation;
    }

    public void setCanStartConversation(boolean canStartConversation) {
        this.canStartConversation = canStartConversation;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public boolean getCanCall() {
        return canCall;
    }

    public void setCanCall(boolean canCall) {
        this.canCall = canCall;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
