package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DocumentAlreadyStoredException extends RuntimeException {

    public DocumentAlreadyStoredException() {
    }

    public DocumentAlreadyStoredException(String message) {
        super(message);
    }

    public DocumentAlreadyStoredException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentAlreadyStoredException(Throwable cause) {
        super(cause);
    }

    public DocumentAlreadyStoredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
