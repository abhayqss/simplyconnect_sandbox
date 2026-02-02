package com.scnsoft.eldermark.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ApplicationException {

    protected HttpStatus httpStatus;

    public InternalServerException(InternalServerExceptionType type, String customMessage) {
        super(customMessage, type.getCode());
        this.httpStatus = type.getHttpStatus();
    }

    public InternalServerException(InternalServerExceptionType type, Throwable cause) {
        super(type.getMessage(), type.getCode(), cause);
        this.httpStatus = type.getHttpStatus();
    }

    public InternalServerException(String message, String code, Throwable cause, HttpStatus httpStatus) {
        super(message, code, cause);
        this.httpStatus = httpStatus;
    }

    public InternalServerException(InternalServerExceptionType type) {
        super(type.getMessage(), type.getCode());
        this.httpStatus = type.getHttpStatus();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
