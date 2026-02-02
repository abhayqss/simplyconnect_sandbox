package com.scnsoft.eldermark.exception;

public class BusinessAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = -3489909213253117327L;

    public BusinessAccessDeniedException() {
    }

    public BusinessAccessDeniedException(String message) {
        super(message);
    }
}
