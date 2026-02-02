package com.scnsoft.eldermark.mobile.dto.signature;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DocumentSignatureRequestRenewDto {

    @JsonIgnore
    private Long requestId;

    private Long expirationDate;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }
}
