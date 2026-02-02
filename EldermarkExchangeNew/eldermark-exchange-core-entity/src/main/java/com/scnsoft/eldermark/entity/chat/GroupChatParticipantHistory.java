package com.scnsoft.eldermark.entity.chat;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "GroupChatParticipantHistory")
public class GroupChatParticipantHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "twilio_conversation_sid", nullable = false)
    private String twilioConversationSid;

    @Column(name = "twilio_participant_sid", nullable = false)
    private String twilioParticipantSid;

    @Column(name = "twilio_identity", nullable = false)
    private String twilioIdentity;

    @Column(name = "added_datetime", nullable = false)
    private Instant addedDatetime;

    @Column(name = "added_by_twilio_identity", nullable = false)
    private String addedByTwilioIdentity;

    @Column(name = "deleted_datetime")
    private Instant deletedDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "deleted_reason")
    private GroupChatParticipantHistoryDeletedReason deletedReason;

    @Column(name = "removed_by_twilio_identity")
    private String removedByTwilioIdentity;

    @Column(name = "client_id")
    private Long clientId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTwilioConversationSid() {
        return twilioConversationSid;
    }

    public void setTwilioConversationSid(String twilioConversationSid) {
        this.twilioConversationSid = twilioConversationSid;
    }

    public String getTwilioParticipantSid() {
        return twilioParticipantSid;
    }

    public void setTwilioParticipantSid(String twilioParticipantSid) {
        this.twilioParticipantSid = twilioParticipantSid;
    }

    public String getTwilioIdentity() {
        return twilioIdentity;
    }

    public void setTwilioIdentity(String twilioIdentity) {
        this.twilioIdentity = twilioIdentity;
    }

    public Instant getAddedDatetime() {
        return addedDatetime;
    }

    public void setAddedDatetime(Instant addedDatetime) {
        this.addedDatetime = addedDatetime;
    }

    public String getAddedByTwilioIdentity() {
        return addedByTwilioIdentity;
    }

    public void setAddedByTwilioIdentity(String addedByTwilioIdentity) {
        this.addedByTwilioIdentity = addedByTwilioIdentity;
    }

    public Instant getDeletedDatetime() {
        return deletedDatetime;
    }

    public void setDeletedDatetime(Instant deletedDatetime) {
        this.deletedDatetime = deletedDatetime;
    }

    public GroupChatParticipantHistoryDeletedReason getDeletedReason() {
        return deletedReason;
    }

    public void setDeletedReason(GroupChatParticipantHistoryDeletedReason deletedReason) {
        this.deletedReason = deletedReason;
    }

    public String getRemovedByTwilioIdentity() {
        return removedByTwilioIdentity;
    }

    public void setRemovedByTwilioIdentity(String removedByTwilioIdentity) {
        this.removedByTwilioIdentity = removedByTwilioIdentity;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
