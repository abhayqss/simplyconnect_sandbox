package com.scnsoft.eldermark.exception;

public class HL7GenerationException extends InternalServerException {

    public HL7GenerationException(Throwable cause) {
        super(InternalServerExceptionType.HL7_GENERATION_EXCEPTION, cause);
    }
}
