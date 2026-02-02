package com.scnsoft.eldermark.dto.conversation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.beans.ConversationType;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConversationTwilioDbSyncCheckResult {

    private String conversationSid;
    private ConversationType conversationType;
    private boolean missingTwilioConversation;

    private boolean disconnectMismatch;
    private Boolean twilioDisconnect;
    private Boolean dbDisconnect;

    private boolean participantsMismatch;
    private Set<String> dbCurrentParticipants;
    private Set<String> twilioParticipants;
    private Set<String> attributesParticipants;

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public boolean isMissingTwilioConversation() {
        return missingTwilioConversation;
    }

    public void setMissingTwilioConversation(boolean missingTwilioConversation) {
        this.missingTwilioConversation = missingTwilioConversation;
    }

    public boolean isDisconnectMismatch() {
        return disconnectMismatch;
    }

    public void setDisconnectMismatch(boolean disconnectMismatch) {
        this.disconnectMismatch = disconnectMismatch;
    }

    public Boolean getTwilioDisconnect() {
        return twilioDisconnect;
    }

    public void setTwilioDisconnect(Boolean twilioDisconnect) {
        this.twilioDisconnect = twilioDisconnect;
    }

    public Boolean getDbDisconnect() {
        return dbDisconnect;
    }

    public void setDbDisconnect(Boolean dbDisconnect) {
        this.dbDisconnect = dbDisconnect;
    }

    public boolean isParticipantsMismatch() {
        return participantsMismatch;
    }

    public void setParticipantsMismatch(boolean participantsMismatch) {
        this.participantsMismatch = participantsMismatch;
    }

    public Set<String> getDbCurrentParticipants() {
        return dbCurrentParticipants;
    }

    public void setDbCurrentParticipants(Set<String> dbCurrentParticipants) {
        this.dbCurrentParticipants = dbCurrentParticipants;
    }

    public Set<String> getTwilioParticipants() {
        return twilioParticipants;
    }

    public void setTwilioParticipants(Set<String> twilioParticipants) {
        this.twilioParticipants = twilioParticipants;
    }

    public Set<String> getAttributesParticipants() {
        return attributesParticipants;
    }

    public void setAttributesParticipants(Set<String> attributesParticipants) {
        this.attributesParticipants = attributesParticipants;
    }

    public boolean isBroken() {
        return missingTwilioConversation || disconnectMismatch || participantsMismatch;
    }
}
