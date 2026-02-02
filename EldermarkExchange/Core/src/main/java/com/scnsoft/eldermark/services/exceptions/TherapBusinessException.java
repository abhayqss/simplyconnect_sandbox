package com.scnsoft.eldermark.services.exceptions;

public class TherapBusinessException extends BusinessException {
    private static final long serialVersionUID = -510378651336374675L;

    public TherapBusinessException(String message) {
        super(message);
    }

    public TherapBusinessException(String message, Throwable e) {
        super(message, e);
    }
}
