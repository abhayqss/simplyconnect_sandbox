package com.scnsoft.eldermark.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends ApplicationException {

    public static final String INTERNAL_SERVER_ERROR_CODE = "internal.server.error";
    public static final String NOT_FOUND_CODE = "not.found";
    public static final String CONSTRAINT_VIOLATION_CODE = "constraint.violation";
    public static final String DATA_INTEGRITY_VIOLATION_CODE = "data.integrity.violation";

    //TODO refactor to have HttpStatus object instead of int code
    protected int httpStatus;

    public BusinessException(BusinessExceptionType type) {
        super(type.message(), type.code());
        this.httpStatus = type.httpStatus();
    }

    public BusinessException(BusinessExceptionType type, Throwable cause) {
        super(type.message(), type.code(), cause);
        this.httpStatus = type.httpStatus();
    }

    public BusinessException(BusinessExceptionType type, String message) {
        super(message, type.code());
        this.httpStatus = type.httpStatus();
    }

    public BusinessException(String code, String message) {
        super(message, code);
    }

    public BusinessException(String code, String message, int httpStatus) {
        super(message, code);
        this.httpStatus = httpStatus;
    }

    public BusinessException(String message) {
        super(message);
        this.code = INTERNAL_SERVER_ERROR_CODE;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, INTERNAL_SERVER_ERROR_CODE, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
}
