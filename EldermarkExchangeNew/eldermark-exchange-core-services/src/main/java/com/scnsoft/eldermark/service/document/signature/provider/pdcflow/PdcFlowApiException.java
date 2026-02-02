package com.scnsoft.eldermark.service.document.signature.provider.pdcflow;

public class PdcFlowApiException extends RuntimeException {

    public PdcFlowApiException() {
        super();
    }

    public PdcFlowApiException(String message) {
        super(message);
    }

    public PdcFlowApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdcFlowApiException(Throwable cause) {
        super(cause);
    }

    protected PdcFlowApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
