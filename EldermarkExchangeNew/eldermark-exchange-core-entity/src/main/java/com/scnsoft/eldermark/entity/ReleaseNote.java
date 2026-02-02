package com.scnsoft.eldermark.entity;


import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ReleaseNote")
public class ReleaseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "modified_date")
    private Instant modifiedDate;

    @Column(name = "title")
    private String title;

    @Column(name = "whats_new")
    private String whatsNew;

    @Column(name = "bug_fixes")
    private String bugFixes;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "description")
    private String description;

    @Column(name = "email_notification_enabled")
    private boolean emailNotificationEnabled;

    @Column(name = "in_app_notification_enabled")
    private boolean inAppNotificationEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWhatsNew() {
        return whatsNew;
    }

    public void setWhatsNew(String whatsNew) {
        this.whatsNew = whatsNew;
    }

    public String getBugFixes() {
        return bugFixes;
    }

    public void setBugFixes(String bugFixes) {
        this.bugFixes = bugFixes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getEmailNotificationEnabled() {
        return emailNotificationEnabled;
    }

    public void setEmailNotificationEnabled(boolean emailNotificationEnabled) {
        this.emailNotificationEnabled = emailNotificationEnabled;
    }

    public boolean getInAppNotificationEnabled() {
        return inAppNotificationEnabled;
    }

    public void setInAppNotificationEnabled(boolean inAppNotificationEnabled) {
        this.inAppNotificationEnabled = inAppNotificationEnabled;
    }
}
