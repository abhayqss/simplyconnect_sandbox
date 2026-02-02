package com.scnsoft.eldermark.hl7v2.exception;

public class RegistryPatientException extends RuntimeException {

    public RegistryPatientException() {
    }

    public RegistryPatientException(String message) {
        super(message);
    }

    public RegistryPatientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryPatientException(Throwable cause) {
        super(cause);
    }

    public RegistryPatientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
