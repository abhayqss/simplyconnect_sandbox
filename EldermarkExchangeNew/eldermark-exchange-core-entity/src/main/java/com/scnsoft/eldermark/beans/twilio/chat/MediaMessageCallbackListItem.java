package com.scnsoft.eldermark.beans.twilio.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaMessageCallbackListItem {

    @JsonProperty("Sid")
    private String sid;

    @JsonProperty("Filename")
    private String fileName;

    @JsonProperty("ContentType")
    private String contentType;

    @JsonProperty("Size")
    private Long size;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
