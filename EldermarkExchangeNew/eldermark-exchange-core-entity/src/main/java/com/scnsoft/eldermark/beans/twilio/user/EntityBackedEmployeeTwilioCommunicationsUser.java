package com.scnsoft.eldermark.beans.twilio.user;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;

import java.util.Set;

public class EntityBackedEmployeeTwilioCommunicationsUser implements EmployeeTwilioCommunicationsUser {

    private final Employee e;

    public EntityBackedEmployeeTwilioCommunicationsUser(Employee e) {
        this.e = e;
    }

    @Override
    public Boolean getOrganizationIsChatEnabled() {
        return e.getOrganization().isChatEnabled();
    }

    @Override
    public Boolean getOrganizationIsVideoEnabled() {
        return e.getOrganization().isVideoEnabled();
    }

    @Override
    public String getTwilioUserSid() {
        return e.getTwilioUserSid();
    }

    @Override
    public void setTwilioUserSid(String twilioUserSid) {
        e.setTwilioUserSid(twilioUserSid);
    }

    @Override
    public String getTwilioServiceConversationSid() {
        return e.getTwilioServiceConversationSid();
    }

    @Override
    public void setTwilioServiceConversationSid(String twilioUserSid) {
        e.setTwilioServiceConversationSid(twilioUserSid);
    }

    @Override
    public Long getId() {
        return e.getId();
    }

    @Override
    public String getFirstName() {
        return e.getFirstName();
    }

    @Override
    public String getLastName() {
        return e.getLastName();
    }

    @Override
    public Long getAvatarId() {
        return e.getAvatar() != null ? e.getAvatar().getId() : null;
    }

    @Override
    public String getAvatarAvatarName() {
        return e.getAvatar() != null ? e.getAvatar().getAvatarName() : null;
    }

    @Override
    public EmployeeStatus getStatus() {
        return e.getStatus();
    }

    @Override
    public Set<Long> getAssociatedClientIds() {
        return e.getAssociatedClientIds();
    }

    @Override
    public Long getCareTeamRoleId() {
        return e.getCareTeamRoleId();
    }

    @Override
    public Long getCommunityId() {
        return e.getCommunityId();
    }

    @Override
    public String getCommunityName() {
        return e.getCommunity().getName();
    }
}
