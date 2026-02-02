package com.scnsoft.eldermark.mobile.dto.careteam;

public class BaseCareTeamContactItem {

    private Long id;
    private Long avatarId;
    private String avatarName;
    private String identity;
    private String twilioUserSid;

    private String fullName;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String statusName;
    private Long communityId;
    private String communityName;
    private String communityLogoName;

    private Long organizationId;
    private String organizationLogoName;

    private boolean canView;

    private boolean canStartConversation;
    private String conversationSid;

    private boolean canCall;

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

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getTwilioUserSid() {
        return twilioUserSid;
    }

    public void setTwilioUserSid(String twilioUserSid) {
        this.twilioUserSid = twilioUserSid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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

    public String getCommunityLogoName() {
        return communityLogoName;
    }

    public void setCommunityLogoName(String communityLogoName) {
        this.communityLogoName = communityLogoName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationLogoName() {
        return organizationLogoName;
    }

    public void setOrganizationLogoName(String organizationLogoName) {
        this.organizationLogoName = organizationLogoName;
    }

    public boolean isCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public boolean isCanStartConversation() {
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

    public boolean isCanCall() {
        return canCall;
    }

    public void setCanCall(boolean canCall) {
        this.canCall = canCall;
    }
}
