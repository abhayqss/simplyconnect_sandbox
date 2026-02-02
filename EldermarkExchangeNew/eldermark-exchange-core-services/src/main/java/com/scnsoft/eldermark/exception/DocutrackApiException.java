package com.scnsoft.eldermark.exception;

public class DocutrackApiException extends InternalServerException {

    public DocutrackApiException() {
        super(InternalServerExceptionType.DOCUTRACK_COMMUNICATION_FAILED);
    }

    public DocutrackApiException(String customMessage) {
        super(InternalServerExceptionType.DOCUTRACK_COMMUNICATION_FAILED, customMessage);
    }

    public DocutrackApiException(Throwable cause) {
        super(InternalServerExceptionType.DOCUTRACK_COMMUNICATION_FAILED, cause);
    }
}
