package com.scnsoft.eldermark.exception;

public class HL7ProcessingException extends InternalServerException {

    public HL7ProcessingException(Throwable cause) {
        super(InternalServerExceptionType.HL7_PROCESSING_EXCEPTION, cause);
    }

    public HL7ProcessingException(String message) {
        super(InternalServerExceptionType.HL7_PROCESSING_EXCEPTION, message);
    }
}
