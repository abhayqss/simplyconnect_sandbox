package com.scnsoft.eldermark.dto;

import org.springframework.web.multipart.MultipartFile;

public class ReleaseNoteDto {

    private Long id;
    private String fileName;
    private String fileMimeType;
    private Long createdDate;
    private MultipartFile file;
    private String features;
    private String fixes;
    private String description;
    private boolean isInAppNotificationEnabled;
    private boolean isEmailNotificationEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMimeType() {
        return fileMimeType;
    }

    public void setFileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getFixes() {
        return fixes;
    }

    public void setFixes(String fixes) {
        this.fixes = fixes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsInAppNotificationEnabled() {
        return isInAppNotificationEnabled;
    }

    public void setIsInAppNotificationEnabled(boolean inAppNotificationEnabled) {
        isInAppNotificationEnabled = inAppNotificationEnabled;
    }

    public boolean getIsEmailNotificationEnabled() {
        return isEmailNotificationEnabled;
    }

    public void setIsEmailNotificationEnabled(boolean emailNotificationEnabled) {
        isEmailNotificationEnabled = emailNotificationEnabled;
    }
}
