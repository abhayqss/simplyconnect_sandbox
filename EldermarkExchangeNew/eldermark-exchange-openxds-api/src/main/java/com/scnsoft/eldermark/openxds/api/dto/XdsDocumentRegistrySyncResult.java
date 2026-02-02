package com.scnsoft.eldermark.openxds.api.dto;

import java.util.List;

public class XdsDocumentRegistrySyncResult {

    private long totalDocumentsFound;
    private List<XdsDocumentRegistrySyncItem> details;

    public long getTotalDocumentsFound() {
        return totalDocumentsFound;
    }

    public void setTotalDocumentsFound(long totalDocumentsFound) {
        this.totalDocumentsFound = totalDocumentsFound;
    }

    public List<XdsDocumentRegistrySyncItem> getDetails() {
        return details;
    }

    public void setDetails(List<XdsDocumentRegistrySyncItem> details) {
        this.details = details;
    }
}
