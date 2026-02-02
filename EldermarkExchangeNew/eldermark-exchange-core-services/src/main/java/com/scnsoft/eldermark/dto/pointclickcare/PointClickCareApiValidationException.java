package com.scnsoft.eldermark.dto.pointclickcare;

public class PointClickCareApiValidationException extends RuntimeException {

    public PointClickCareApiValidationException() {
    }

    public PointClickCareApiValidationException(String message) {
        super(message);
    }

    public PointClickCareApiValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PointClickCareApiValidationException(Throwable cause) {
        super(cause);
    }

    public PointClickCareApiValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
