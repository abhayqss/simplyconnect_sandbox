package com.scnsoft.eldermark.openxds.api.exceptions;

public class OpenXdsApiException extends RuntimeException {

    public OpenXdsApiException(String message) {
        super(message);
    }

    public OpenXdsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
