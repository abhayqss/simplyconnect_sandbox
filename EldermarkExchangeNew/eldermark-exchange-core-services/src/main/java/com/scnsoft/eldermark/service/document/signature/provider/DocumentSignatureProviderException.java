package com.scnsoft.eldermark.service.document.signature.provider;

public class DocumentSignatureProviderException extends RuntimeException {
    public DocumentSignatureProviderException() {
    }

    public DocumentSignatureProviderException(String message) {
        super(message);
    }

    public DocumentSignatureProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentSignatureProviderException(Throwable cause) {
        super(cause);
    }

    public DocumentSignatureProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
