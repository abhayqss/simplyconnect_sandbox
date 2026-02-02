package com.scnsoft.scansol.shared;

/**
 * Date: 14.05.15
 * Time: 7:54
 */
public class ScanSolUploadDocumentRequest {

    private long employeeId;
    private String originalName;
    private String title;
    private String mimeType;
    private byte[] content;
    private Boolean shared;

    public long getEmployeeId () {
        return employeeId;
    }

    public void setEmployeeId (long employeeId) {
        this.employeeId = employeeId;
    }

    public String getOriginalName () {
        return originalName;
    }

    public void setOriginalName (String originalName) {
        this.originalName = originalName;
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

    public byte[] getContent () {
        return content;
    }

    public void setContent (byte[] content) {
        this.content = content;
    }

    public Boolean isShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }
}
