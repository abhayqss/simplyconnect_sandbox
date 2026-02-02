package com.scnsoft.eldermark.dto.document;

public class DocumentSignatureDto {

    private String statusName;
    private String statusTitle;
    private Long signedDate;
    private Long requestedDate;
    private Long bulkRequestId;

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(Long signedDate) {
        this.signedDate = signedDate;
    }

    public Long getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Long requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Long getBulkRequestId() {
        return bulkRequestId;
    }

    public void setBulkRequestId(Long bulkRequestId) {
        this.bulkRequestId = bulkRequestId;
    }
}
