package com.scnsoft.eldermark.dto.pointclickcare;

public class PointClickCareUnknownLocationException extends RuntimeException {

    public PointClickCareUnknownLocationException() {
    }

    public PointClickCareUnknownLocationException(String message) {
        super(message);
    }

    public PointClickCareUnknownLocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PointClickCareUnknownLocationException(Throwable cause) {
        super(cause);
    }

    public PointClickCareUnknownLocationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
