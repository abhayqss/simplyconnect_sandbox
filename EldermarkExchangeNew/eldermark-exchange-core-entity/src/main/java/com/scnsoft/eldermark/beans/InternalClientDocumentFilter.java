package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;

import java.util.List;

public class InternalClientDocumentFilter {
    private String title;
    private Long clientId;
    private String description;
    private List<Long> categoryIds;
    private Long fromDate;
    private Long toDate;
    private List<DocumentSignatureRequestStatus> signatureStatuses;
    private boolean includeNotCategorized;
    private boolean includeDeleted;
    private boolean includeWithoutSignature;
    private boolean includeSearchByCategoryName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public List<DocumentSignatureRequestStatus> getSignatureStatuses() {
        return signatureStatuses;
    }

    public void setSignatureStatuses(List<DocumentSignatureRequestStatus> signatureStatuses) {
        this.signatureStatuses = signatureStatuses;
    }

    public boolean getIncludeNotCategorized() {
        return includeNotCategorized;
    }

    public void setIncludeNotCategorized(boolean includeNotCategorized) {
        this.includeNotCategorized = includeNotCategorized;
    }

    public boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public boolean getIncludeWithoutSignature() {
        return includeWithoutSignature;
    }

    public void setIncludeWithoutSignature(boolean includeWithoutSignature) {
        this.includeWithoutSignature = includeWithoutSignature;
    }

    public boolean getIncludeSearchByCategoryName() {
        return includeSearchByCategoryName;
    }

    public void setIncludeSearchByCategoryName(boolean includeSearchByCategoryName) {
        this.includeSearchByCategoryName = includeSearchByCategoryName;
    }
}
