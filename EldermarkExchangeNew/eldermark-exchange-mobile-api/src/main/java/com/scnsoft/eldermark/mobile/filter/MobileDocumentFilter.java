package com.scnsoft.eldermark.mobile.filter;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;

import java.util.List;

public class MobileDocumentFilter {

    private Long clientId;
    private boolean includeGenerated;
    private List<DocumentSignatureStatus> signatureStatuses;
    private boolean includeWithoutSignature;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public boolean isIncludeGenerated() {
        return includeGenerated;
    }

    public void setIncludeGenerated(boolean includeGenerated) {
        this.includeGenerated = includeGenerated;
    }

    public List<DocumentSignatureStatus> getSignatureStatuses() {
        return signatureStatuses;
    }

    public void setSignatureStatuses(List<DocumentSignatureStatus> signatureStatuses) {
        this.signatureStatuses = signatureStatuses;
    }

    public boolean isIncludeWithoutSignature() {
        return includeWithoutSignature;
    }

    public void setIncludeWithoutSignature(boolean includeWithoutSignature) {
        this.includeWithoutSignature = includeWithoutSignature;
    }
}
