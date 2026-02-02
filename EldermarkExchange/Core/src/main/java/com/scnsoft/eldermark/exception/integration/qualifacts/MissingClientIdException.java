package com.scnsoft.eldermark.exception.integration.qualifacts;

public class MissingClientIdException extends QualifactsDocumentsGatewayException {
    public MissingClientIdException() {
    }

    public MissingClientIdException(String message) {
        super(message);
    }

    public MissingClientIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingClientIdException(Throwable cause) {
        super(cause);
    }

    public MissingClientIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
