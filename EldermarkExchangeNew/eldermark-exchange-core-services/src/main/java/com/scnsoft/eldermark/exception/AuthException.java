package com.scnsoft.eldermark.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends InternalServerException {
    public AuthException(InternalServerExceptionType type, Throwable cause) {
        super(type, cause);
    }

    public AuthException(InternalServerExceptionType type, String customMessage) {
        super(type, customMessage);
    }

    public AuthException(String message, String code, Throwable cause, HttpStatus httpStatus) {
        super(message, code, cause, httpStatus);
    }

    public AuthException(InternalServerExceptionType type) {
        super(type);
    }
}
