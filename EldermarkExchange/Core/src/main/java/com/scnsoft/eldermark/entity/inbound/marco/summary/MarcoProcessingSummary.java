package com.scnsoft.eldermark.entity.inbound.marco.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;

public class MarcoProcessingSummary extends ProcessingSummary {

    private String fileName;
    private String metadataFileName;
    private boolean isAssigned;
    private Long documentId;
    private Long residentId;
    private Long marcoIntegrationDocumentId;

    @JsonIgnore
    private MarcoIntegrationDocument marcoIntegrationDocument;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMetadataFileName() {
        return metadataFileName;
    }

    public void setMetadataFileName(String metadataFileName) {
        this.metadataFileName = metadataFileName;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
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

    public Long getMarcoIntegrationDocumentId() {
        return marcoIntegrationDocumentId;
    }

    public void setMarcoIntegrationDocumentId(Long marcoIntegrationDocumentId) {
        this.marcoIntegrationDocumentId = marcoIntegrationDocumentId;
    }

    public MarcoIntegrationDocument getMarcoIntegrationDocument() {
        return marcoIntegrationDocument;
    }

    public void setMarcoIntegrationDocument(MarcoIntegrationDocument marcoIntegrationDocument) {
        this.marcoIntegrationDocument = marcoIntegrationDocument;
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
