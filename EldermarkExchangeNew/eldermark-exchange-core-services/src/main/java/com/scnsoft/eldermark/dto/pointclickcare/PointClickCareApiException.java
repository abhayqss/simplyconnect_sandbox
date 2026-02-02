package com.scnsoft.eldermark.dto.pointclickcare;

public class PointClickCareApiException extends RuntimeException {

    public PointClickCareApiException() {
    }

    public PointClickCareApiException(String message) {
        super(message);
    }

    public PointClickCareApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public PointClickCareApiException(Throwable cause) {
        super(cause);
    }

    public PointClickCareApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
