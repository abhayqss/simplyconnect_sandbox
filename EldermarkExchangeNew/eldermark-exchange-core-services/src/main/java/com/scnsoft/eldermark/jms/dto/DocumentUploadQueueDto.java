package com.scnsoft.eldermark.jms.dto;

import com.scnsoft.eldermark.entity.document.SharingOption;

public class DocumentUploadQueueDto {
    private String title;
    private String originalFileName;
    private String mimeType;
    private byte[] data;
    private Long clientId;
    private Long authorId;
    private SharingOption sharingOption;
    private String consanaMapId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public SharingOption getSharingOption() {
        return sharingOption;
    }

    public void setSharingOption(SharingOption sharingOption) {
        this.sharingOption = sharingOption;
    }

    public String getConsanaMapId() {
        return consanaMapId;
    }

    public void setConsanaMapId(String consanaMapId) {
        this.consanaMapId = consanaMapId;
    }
}
