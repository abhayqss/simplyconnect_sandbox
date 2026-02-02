package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.beans.ConversationType;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "MissedChatsAndCalls")
public class MissedChatsAndCalls {

    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private String id;

    @Column(name = "conversation_sid", nullable = false, insertable = false, updatable = false)
    private String conversationSid;

    @Column(name = "call_history_id", insertable = false, updatable = false)
    private Long callHistoryId;

    @Column(name = "friendly_conversation_name", insertable = false, updatable = false)
    private String friendlyConversationName;

    @Column(name = "date_time", insertable = false, updatable = false)
    private Instant dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", insertable = false, updatable = false, nullable = false)
    private ConversationType conversationType;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public Long getCallHistoryId() {
        return callHistoryId;
    }

    public void setCallHistoryId(Long callHistoryId) {
        this.callHistoryId = callHistoryId;
    }

    public String getFriendlyConversationName() {
        return friendlyConversationName;
    }

    public void setFriendlyConversationName(String friendlyConversationName) {
        this.friendlyConversationName = friendlyConversationName;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

}
