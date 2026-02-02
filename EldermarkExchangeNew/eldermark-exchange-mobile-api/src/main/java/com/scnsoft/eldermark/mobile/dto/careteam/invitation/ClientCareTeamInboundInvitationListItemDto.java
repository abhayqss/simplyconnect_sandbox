package com.scnsoft.eldermark.mobile.dto.careteam.invitation;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import org.springframework.data.domain.Sort;

public class ClientCareTeamInboundInvitationListItemDto {

    private Long id;

    @Deprecated
    private String firstName;
    private String clientFirstName;
    @Deprecated
    private String lastName;
    private String clientLastName;

    @Deprecated
    private String communityName;
    private String clientCommunityName;

    @Deprecated
    private Long avatarId;
    private Long clientAvatarId;
    @Deprecated
    private String avatarName;
    private String clientAvatarName;

    @Deprecated
    private String twilioUserSid;
    private String clientTwilioUserSid;

    @DefaultSort(direction = Sort.Direction.DESC)
    private Long createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    @Deprecated
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Deprecated
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCommunityName() {
        return communityName;
    }

    @Deprecated
    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    @Deprecated
    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    @Deprecated
    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getTwilioUserSid() {
        return twilioUserSid;
    }

    @Deprecated
    public void setTwilioUserSid(String twilioUserSid) {
        this.twilioUserSid = twilioUserSid;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public String getClientCommunityName() {
        return clientCommunityName;
    }

    public void setClientCommunityName(String clientCommunityName) {
        this.clientCommunityName = clientCommunityName;
    }

    public Long getClientAvatarId() {
        return clientAvatarId;
    }

    public void setClientAvatarId(Long clientAvatarId) {
        this.clientAvatarId = clientAvatarId;
    }

    public String getClientAvatarName() {
        return clientAvatarName;
    }

    public void setClientAvatarName(String clientAvatarName) {
        this.clientAvatarName = clientAvatarName;
    }

    public String getClientTwilioUserSid() {
        return clientTwilioUserSid;
    }

    public void setClientTwilioUserSid(String clientTwilioUserSid) {
        this.clientTwilioUserSid = clientTwilioUserSid;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
