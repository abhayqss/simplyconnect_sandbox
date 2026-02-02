package com.scnsoft.eldermark.beans.twilio.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.beans.ConversationType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ConversationAttributes {

    private ConversationType type;
    private Set<String> participantIdentities;

    private Long participatingClientId;
    private String associatedContactIdentity;

    //we can't use conversation's friendlyName because it is impossible to set it back
    //to "" or null. This is twilio API strange limitation
    private String friendlyName;

    //fields related to Incident Report conversation
    private Long incidentReportId;

    @Deprecated //irClientId
    private Long clientId;
    @Deprecated //irClientCommunityId
    private Long communityId;

    //todo - update existing conversations' attributes
    private Long irClientId;
    private Long irClientCommunityId;

    private String roomSid;

    private boolean disconnected;

    private Map<String, Object> restProps = new HashMap<>();

    public ConversationType getType() {
        return type;
    }

    public ConversationAttributes setType(ConversationType type) {
        this.type = type;
        return this;
    }

    public Set<String> getParticipantIdentities() {
        return participantIdentities;
    }

    public ConversationAttributes setParticipantIdentities(Set<String> participantIdentities) {
        this.participantIdentities = participantIdentities;
        return this;
    }

    public Long getParticipatingClientId() {
        return participatingClientId;
    }

    public ConversationAttributes setParticipatingClientId(Long participatingClientId) {
        this.participatingClientId = participatingClientId;
        return this;
    }

    public String getAssociatedContactIdentity() {
        return associatedContactIdentity;
    }

    public ConversationAttributes setAssociatedContactIdentity(String associatedContactIdentity) {
        this.associatedContactIdentity = associatedContactIdentity;
        return this;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public ConversationAttributes setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        return this;
    }

    public Long getIncidentReportId() {
        return incidentReportId;
    }

    public ConversationAttributes setIncidentReportId(Long incidentReportId) {
        this.incidentReportId = incidentReportId;
        return this;
    }

    @Deprecated
    public Long getClientId() {
        return clientId;
    }

    @Deprecated
    public ConversationAttributes setClientId(Long clientId) {
        this.clientId = clientId;
        return this;
    }

    @Deprecated
    public Long getCommunityId() {
        return communityId;
    }

    @Deprecated
    public ConversationAttributes setCommunityId(Long communityId) {
        this.communityId = communityId;
        return this;
    }

    public Long getIrClientId() {
        return irClientId;
    }

    public ConversationAttributes setIrClientId(Long irClientId) {
        this.irClientId = irClientId;
        return this;
    }

    public Long getIrClientCommunityId() {
        return irClientCommunityId;
    }

    public ConversationAttributes setIrClientCommunityId(Long irClientCommunityId) {
        this.irClientCommunityId = irClientCommunityId;
        return this;
    }

    public String getRoomSid() {
        return roomSid;
    }

    public void setRoomSid(String roomSid) {
        this.roomSid = roomSid;
    }

    public boolean getDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }
}
