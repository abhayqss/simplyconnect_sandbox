package com.scnsoft.eldermark.beans.twilio.user;

import com.scnsoft.eldermark.beans.projection.NamesAware;

public class IdentityListItemDto implements NamesAware {
    private String identity;
    private Long employeeId;
    private String firstName;
    private String lastName;
    private Long communityId;
    private String communityName;
    private Long avatarId;
    private String avatarName;
    private Boolean isActive;
    private Boolean canChat;
    private Boolean canCall;

    public IdentityListItemDto(
            String identity,
            Long employeeId,
            String firstName,
            String lastName,
            Long communityId,
            String communityName,
            Long avatarId,
            String avatarName,
            Boolean isActive,
            Boolean canChat,
            Boolean canCall
    ) {
        this.identity = identity;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.communityId = communityId;
        this.communityName = communityName;
        this.avatarId = avatarId;
        this.avatarName = avatarName;
        this.isActive = isActive;
        this.canChat = canChat;
        this.canCall = canCall;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Boolean getCanChat() {
        return canChat;
    }

    public void setCanChat(Boolean canChat) {
        this.canChat = canChat;
    }

    public Boolean getCanCall() {
        return canCall;
    }

    public void setCanCall(Boolean canCall) {
        this.canCall = canCall;
    }

    @Override
    public String toString() {
        return "IdentityListItemDto{" +
                "identity='" + identity + '\'' +
                ", employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", communityId='" + communityId + '\'' +
                ", communityName='" + communityName + '\'' +
                ", avatarId=" + avatarId +
                ", isActive=" + isActive +
                '}';
    }
}
