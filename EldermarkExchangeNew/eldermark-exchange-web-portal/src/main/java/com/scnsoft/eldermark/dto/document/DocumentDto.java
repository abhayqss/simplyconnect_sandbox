package com.scnsoft.eldermark.dto.document;

public class DocumentDto extends BaseDocumentDto {

    private Long temporarilyDeletedDate;

    private String temporarilyDeletedBy;

    private Long assignedDate;

    private String assignedBy;

    private Long templateId;

    private DocumentSignatureInfoDto signature;

    private Long bulkRequestId;

    public Long getTemporarilyDeletedDate() {
        return temporarilyDeletedDate;
    }

    public void setTemporarilyDeletedDate(Long temporarilyDeletedDate) {
        this.temporarilyDeletedDate = temporarilyDeletedDate;
    }

    public String getTemporarilyDeletedBy() {
        return temporarilyDeletedBy;
    }

    public void setTemporarilyDeletedBy(String temporarilyDeletedBy) {
        this.temporarilyDeletedBy = temporarilyDeletedBy;
    }

    public Long getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Long assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public DocumentSignatureInfoDto getSignature() {
        return signature;
    }

    public void setSignature(DocumentSignatureInfoDto signature) {
        this.signature = signature;
    }

    public Long getBulkRequestId() {
        return bulkRequestId;
    }

    public void setBulkRequestId(Long bulkRequestId) {
        this.bulkRequestId = bulkRequestId;
    }
}
