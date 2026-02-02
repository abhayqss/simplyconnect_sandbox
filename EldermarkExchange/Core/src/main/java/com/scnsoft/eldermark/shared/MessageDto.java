package com.scnsoft.eldermark.shared;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MessageDto implements Serializable, Comparable<MessageDto> {
    private String messageId;

    private String from;

    private List<String> to;

    private String subject;

    private String body;

    private List<MessageAttachmentDto> attachments;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date date;

    private Boolean seen;

    private DirectMessageType messageType;

    public MessageDto() {
    }

    public String getId() {
        return messageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getToAsString(){
        return StringUtils.join(to, "; ");
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<MessageAttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MessageAttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public DirectMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(DirectMessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public int compareTo(MessageDto o) {
        return ObjectUtils.compare(o.getDate(), this.getDate());
    }
}
