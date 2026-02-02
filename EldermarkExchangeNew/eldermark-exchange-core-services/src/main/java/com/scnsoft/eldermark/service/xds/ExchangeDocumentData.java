package com.scnsoft.eldermark.service.xds;

import java.util.Date;

public class ExchangeDocumentData {

    private Integer size;
    private String hash;
    private Date createTime;
    private String mimeType;
    private String uuid;
    private String patientId;
    private String docTitle;
    private Boolean visible;
    private Boolean shared;
    private String uniqueId;

    public ExchangeDocumentData(String uniqueId, String hash, Integer size, Date createTime, String mimeType, String uuid, String patientId, String docTitle, Boolean visible, Boolean shared) {
        this.uniqueId = uniqueId;
        this.hash = hash;
        this.size = size;
        this.createTime = createTime;
        this.mimeType = mimeType;
        this.uuid = uuid;
        this.patientId = patientId;
        this.docTitle = docTitle;
        this.visible = visible;
        this.shared = shared;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
