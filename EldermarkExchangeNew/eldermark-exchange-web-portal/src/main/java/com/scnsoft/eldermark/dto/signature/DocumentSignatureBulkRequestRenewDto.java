package com.scnsoft.eldermark.dto.signature;

import javax.validation.constraints.NotNull;

public class DocumentSignatureBulkRequestRenewDto {

    private Long bulkRequestId;

    @NotNull
    private Long expirationDate;

    @NotNull
    private Long templateId;

    public Long getBulkRequestId() {
        return bulkRequestId;
    }

    public void setBulkRequestId(Long bulkRequestId) {
        this.bulkRequestId = bulkRequestId;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}

