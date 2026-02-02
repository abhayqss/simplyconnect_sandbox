package com.scnsoft.eldermark.entity.signature;

public enum PdcFlowSignatureStatus {
    // The request has been sent.
    PENDING,
    // A valid PIN has been entered on the request, but the request has not been completed.
    OPEN,
    // The request has been successfully completed.
    COMPLETED,
    // The request has failed. This can be due to the document being denied, the request being manually closed, etc. An errorCode and errorMessage will provide the reason for the failure.
    FAILED,
    // The request has exceeded the timeoutMinutes and cannot be completed.
    EXPIRED
}
