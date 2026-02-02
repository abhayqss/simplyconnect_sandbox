package com.scnsoft.eldermark.exception;

import org.springframework.http.HttpStatus;

public enum WebApplicationExceptionType {
    
    NOT_FOUND("not.found", "The specified entity can not be found", HttpStatus.NOT_FOUND.value());
    
    private final String code;
    private final String message;
    private final int httpStatus;

    WebApplicationExceptionType(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public int httpStatus() {
        return httpStatus;
    }

}
