package com.scnsoft.eldermark.entity.chat;

import com.scnsoft.eldermark.beans.ConversationType;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "TwilioConversation")
public class TwilioConversation {

    @Id
    @Column(name = "twilio_conversation_sid", nullable = false)
    private String twilioConversationSid;

    @Column(name = "friendly_conversation_name")
    private String friendlyConversationName;

    @Column(name = "last_message_index", nullable = false, columnDefinition = "bigint")
    private Integer lastMessageIndex;

    @Column(name = "last_message_datetime")
    private Instant lastMessageDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", nullable = false)
    private ConversationType conversationType;

    @Column(name = "date_created")
    private Instant dateCreated;

    @Column(name = "disconnected", nullable = false)
    private boolean disconnected;

    public String getTwilioConversationSid() {
        return twilioConversationSid;
    }

    public void setTwilioConversationSid(String twilioConversationSid) {
        this.twilioConversationSid = twilioConversationSid;
    }

    public String getFriendlyConversationName() {
        return friendlyConversationName;
    }

    public void setFriendlyConversationName(String friendlyConversationName) {
        this.friendlyConversationName = friendlyConversationName;
    }

    public Integer getLastMessageIndex() {
        return lastMessageIndex;
    }

    public void setLastMessageIndex(Integer lastMessageIndex) {
        this.lastMessageIndex = lastMessageIndex;
    }

    public Instant getLastMessageDatetime() {
        return lastMessageDatetime;
    }

    public void setLastMessageDatetime(Instant lastMessageDatetime) {
        this.lastMessageDatetime = lastMessageDatetime;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean getDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }
}
