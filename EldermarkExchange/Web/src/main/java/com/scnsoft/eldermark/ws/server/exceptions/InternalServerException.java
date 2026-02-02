package com.scnsoft.eldermark.ws.server.exceptions;

/**
 * Indicates unexpected server errors.
 */
public class InternalServerException extends RuntimeException {
    public InternalServerException() {
        this("Internal server error");
    }

    public InternalServerException(String message) {
        super(message);
    }
}
