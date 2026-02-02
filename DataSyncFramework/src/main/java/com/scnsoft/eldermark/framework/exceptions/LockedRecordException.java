package com.scnsoft.eldermark.framework.exceptions;

public class LockedRecordException extends DataAccessException {
    public LockedRecordException(String message) {
        super(message);
    }

    public LockedRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
