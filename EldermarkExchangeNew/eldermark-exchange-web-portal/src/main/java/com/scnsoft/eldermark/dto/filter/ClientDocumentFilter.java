package com.scnsoft.eldermark.dto.filter;

import javax.validation.constraints.Size;
import java.util.List;

public class ClientDocumentFilter {
    @Size(max = 256)
    private String title;
    private Long clientId;
    @Size(max = 3950)
    private String description;
    private List<Long> categoryIds;
    private List<String> signatureStatusNames;
    private Long fromDate;
    private Long toDate;
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

    public List<String> getSignatureStatusNames() {
        return signatureStatusNames;
    }

    public void setSignatureStatusNames(List<String> signatureStatusNames) {
        this.signatureStatusNames = signatureStatusNames;
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
