package com.scnsoft.eldermark.entity.lab.review;

import java.util.Objects;

public class LabResearchOrderDocument {
    private Long documentId;
    private String documentTitle;
    private String documentOriginalFileName;
    private String mimeType;

    public LabResearchOrderDocument(Long documentId, String documentTitle, String documentOriginalFileName, String mimeType) {
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.documentOriginalFileName = documentOriginalFileName;
        this.mimeType = mimeType;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentOriginalFileName() {
        return documentOriginalFileName;
    }

    public void setDocumentOriginalFileName(String documentOriginalFileName) {
        this.documentOriginalFileName = documentOriginalFileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabResearchOrderDocument)) return false;
        LabResearchOrderDocument that = (LabResearchOrderDocument) o;
        return Objects.equals(documentId, that.documentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId);
    }
}
