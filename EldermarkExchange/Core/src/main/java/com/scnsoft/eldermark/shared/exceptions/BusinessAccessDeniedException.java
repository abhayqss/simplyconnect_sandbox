package com.scnsoft.eldermark.shared.exceptions;

public class BusinessAccessDeniedException extends RuntimeException {
    public BusinessAccessDeniedException() {
    }

    public BusinessAccessDeniedException(String message) {
        super(message);
    }
}
