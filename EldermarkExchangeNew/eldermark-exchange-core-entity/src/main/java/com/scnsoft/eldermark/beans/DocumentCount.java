package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;

public class DocumentCount {

    private DocumentSignatureRequestStatus status;
    private Long count;

    public DocumentCount(DocumentSignatureRequestStatus status, Long count) {
        this.status = status;
        this.count = count;
    }

    public DocumentSignatureRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentSignatureRequestStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
