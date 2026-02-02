package com.scnsoft.eldermark.services.exceptions.hl7;

public class HL7ConversionException extends RuntimeException {

    public HL7ConversionException() {
    }

    public HL7ConversionException(String message) {
        super(message);
    }

    public HL7ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public HL7ConversionException(Throwable cause) {
        super(cause);
    }

    public HL7ConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
