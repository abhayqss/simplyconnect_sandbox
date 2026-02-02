package com.scnsoft.eldermark.dto.signature;

public class DocumentSignatureTemplateAssignToFolderDto {
    private Long folderId;
    private Long templateId;

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}