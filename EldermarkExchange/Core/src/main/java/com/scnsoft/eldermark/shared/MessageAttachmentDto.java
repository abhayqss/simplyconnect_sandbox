package com.scnsoft.eldermark.shared;

import java.io.Serializable;

public class MessageAttachmentDto implements Serializable {
    private Integer partIndex;

    private String messageId;

    private String name;

    private String contentType;

    public MessageAttachmentDto() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getPartIndex() {
        return partIndex;
    }

    public void setPartIndex(Integer partIndex) {
        this.partIndex = partIndex;
    }
}
