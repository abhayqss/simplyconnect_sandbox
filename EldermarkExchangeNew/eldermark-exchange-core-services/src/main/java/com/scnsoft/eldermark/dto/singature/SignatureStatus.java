package com.scnsoft.eldermark.dto.singature;

import com.scnsoft.eldermark.entity.signature.PdcFlowSignatureStatus;

import java.time.LocalDateTime;

public class SignatureStatus {
    private String errorCode;
    private String errorMessage;
    private PdcFlowSignatureStatus status;
    private LocalDateTime statusDate;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public PdcFlowSignatureStatus getStatus() {
        return status;
    }

    public void setStatus(PdcFlowSignatureStatus status) {
        this.status = status;
    }

    public LocalDateTime getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(LocalDateTime statusDate) {
        this.statusDate = statusDate;
    }
}
