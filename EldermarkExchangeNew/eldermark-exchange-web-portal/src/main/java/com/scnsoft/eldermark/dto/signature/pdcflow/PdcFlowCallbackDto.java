package com.scnsoft.eldermark.dto.signature.pdcflow;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class PdcFlowCallbackDto {
    private BigInteger signatureId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completionDate;

    private String errorCode;

    private String errorMessage;

    public BigInteger getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(BigInteger signatureId) {
        this.signatureId = signatureId;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

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

    @Override
    public String toString() {
        return "PdcFlowCallbackDto{" +
                "signatureId=" + signatureId +
                ", completionDate=" + completionDate +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
