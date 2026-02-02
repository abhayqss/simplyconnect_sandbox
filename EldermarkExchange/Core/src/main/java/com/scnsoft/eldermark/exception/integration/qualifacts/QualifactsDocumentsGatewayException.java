package com.scnsoft.eldermark.exception.integration.qualifacts;

public class QualifactsDocumentsGatewayException extends RuntimeException {

    public QualifactsDocumentsGatewayException() {
    }

    public QualifactsDocumentsGatewayException(String message) {
        super(message);
    }

    public QualifactsDocumentsGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public QualifactsDocumentsGatewayException(Throwable cause) {
        super(cause);
    }

    public QualifactsDocumentsGatewayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
