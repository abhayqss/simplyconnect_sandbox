package com.scnsoft.eldermark.beans.twilio.video;

import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;

import java.util.List;
import java.util.Set;

public class InitiateCallOutcome {
    private boolean areCalleesBusy;
    private IdentityListItemDto caller;
    private List<IdentityListItemDto> callees;
    private Set<String> pendingIdentities;
    private String roomSid;
    private String roomAccessToken;
    private String conversationSid;
    private String conversationFriendlyName;

    public boolean getAreCalleesBusy() {
        return areCalleesBusy;
    }

    public void setAreCalleesBusy(boolean areCalleesBusy) {
        this.areCalleesBusy = areCalleesBusy;
    }

    public IdentityListItemDto getCaller() {
        return caller;
    }

    public void setCaller(IdentityListItemDto caller) {
        this.caller = caller;
    }

    public List<IdentityListItemDto> getCallees() {
        return callees;
    }

    public void setCallees(List<IdentityListItemDto> callees) {
        this.callees = callees;
    }

    public Set<String> getPendingIdentities() {
        return pendingIdentities;
    }

    public void setPendingIdentities(Set<String> pendingIdentities) {
        this.pendingIdentities = pendingIdentities;
    }

    public String getRoomSid() {
        return roomSid;
    }

    public void setRoomSid(String roomSid) {
        this.roomSid = roomSid;
    }

    public String getRoomAccessToken() {
        return roomAccessToken;
    }

    public void setRoomAccessToken(String roomAccessToken) {
        this.roomAccessToken = roomAccessToken;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public String getConversationFriendlyName() {
        return conversationFriendlyName;
    }

    public void setConversationFriendlyName(String conversationFriendlyName) {
        this.conversationFriendlyName = conversationFriendlyName;
    }
}
