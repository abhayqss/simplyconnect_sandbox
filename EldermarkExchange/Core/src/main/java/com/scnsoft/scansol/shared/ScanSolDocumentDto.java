package com.scnsoft.scansol.shared;

/**
 * Date: 19.05.15
 * Time: 5:49
 */
public class ScanSolDocumentDto {
    private Long id;
    private String length;
    private String title;
    private String mimeType;
    private String originalName;
    private String creationTime;
    private ScanSolBaseEmployeeInfoDto author;
    private Boolean visible;
    private Boolean shared;

    public ScanSolBaseEmployeeInfoDto getAuthor () {
        return author;
    }

    public void setAuthor (ScanSolBaseEmployeeInfoDto author) {
        this.author = author;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getLength () {
        return length;
    }

    public void setLength (String length) {
        this.length = length;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getMimeType () {
        return mimeType;
    }

    public void setMimeType (String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOriginalName () {
        return originalName;
    }

    public void setOriginalName (String originalName) {
        this.originalName = originalName;
    }

    public String getCreationTime () {
        return creationTime;
    }

    public void setCreationTime (String creationTime) {
        this.creationTime = creationTime;
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
}
