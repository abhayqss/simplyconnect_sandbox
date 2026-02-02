package com.scnsoft.eldermark.exception.integration.qualifacts;

public class DocumentWithoutResidentException extends QualifactsDocumentsGatewayException {
    public DocumentWithoutResidentException() {
    }

    public DocumentWithoutResidentException(String message) {
        super(message);
    }

    public DocumentWithoutResidentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentWithoutResidentException(Throwable cause) {
        super(cause);
    }

    public DocumentWithoutResidentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
