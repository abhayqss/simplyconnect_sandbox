package com.scnsoft.eldermark.services.exceptions;

/**
 * Created by knetkachou on 10/21/2016.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
    }
}
