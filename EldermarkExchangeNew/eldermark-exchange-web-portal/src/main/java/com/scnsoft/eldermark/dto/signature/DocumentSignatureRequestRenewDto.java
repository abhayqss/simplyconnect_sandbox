package com.scnsoft.eldermark.dto.signature;

import javax.validation.constraints.NotNull;

public class DocumentSignatureRequestRenewDto {

    private Long requestId;

    @NotNull
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

