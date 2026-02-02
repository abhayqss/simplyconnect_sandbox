package com.scnsoft.eldermark.entity.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Chat")
public class AuditLogChatRelation extends AuditLogRelation<String> {

    @Column(name = "conversation_sid", nullable = false)
    private String conversationSid;

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    @Override
    public List<String> getRelatedIds() {
        return List.of(conversationSid);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.CHAT;
    }
}