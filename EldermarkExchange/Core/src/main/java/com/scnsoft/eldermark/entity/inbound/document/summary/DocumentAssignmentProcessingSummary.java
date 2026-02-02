package com.scnsoft.eldermark.entity.inbound.document.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentLog;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;

public class DocumentAssignmentProcessingSummary extends ProcessingSummary {

    private String fileName;
    private boolean assigned;
    private Long documentId;
    private Long residentId;
    private Long documentAssignmentLogId;

    @JsonIgnore
    private DocumentAssignmentLog documentAssignmentLog;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean getAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getDocumentAssignmentLogId() {
        return documentAssignmentLogId;
    }

    public void setDocumentAssignmentLogId(Long documentAssignmentLogId) {
        this.documentAssignmentLogId = documentAssignmentLogId;
    }

    public DocumentAssignmentLog getDocumentAssignmentLog() {
        return documentAssignmentLog;
    }

    public void setDocumentAssignmentLog(DocumentAssignmentLog documentAssignmentLog) {
        this.documentAssignmentLog = documentAssignmentLog;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return true;
    }

    @Override
    protected String buildWarnMessage() {
        return null;
    }
}
