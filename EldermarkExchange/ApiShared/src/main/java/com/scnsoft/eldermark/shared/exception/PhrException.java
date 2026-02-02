package com.scnsoft.eldermark.shared.exception;

import org.apache.http.HttpStatus;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 12/27/2016.
 */
public class PhrException extends RuntimeException {
    public static final String INTERNAL_SERVER_ERROR_CODE = "internal.server.error";
    public static final String NOT_FOUND_CODE = "not.found";
    public static final String CONSTRAINT_VIOLATION_CODE = "constraint.violation";
    public static final String DATA_INTEGRITY_VIOLATION_CODE = "data.integrity.violation";

    protected String code;
    protected int httpStatus;

    public PhrException(PhrExceptionType type) {
        super(type.message());
        this.code = type.code();
        this.httpStatus = type.httpStatus();
    }

    public PhrException(PhrExceptionType type, Throwable cause) {
        super(type.message(), cause);
        this.code = type.code();
        this.httpStatus = type.httpStatus();
    }

    public PhrException(PhrExceptionType type, String message) {
        super(message);
        this.code = type.code();
        this.httpStatus = type.httpStatus();
    }

    public PhrException(String code, String message) {
        super(message);
        this.code = code;
    }

    public PhrException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public PhrException(String message) {
        super(message);
        this.code = INTERNAL_SERVER_ERROR_CODE;
        this.httpStatus = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    public PhrException() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
}
