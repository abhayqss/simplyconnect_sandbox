package com.scnsoft.eldermark.dao.exceptions;

public class InitialSyncNotCompletedException extends RuntimeException {

    public InitialSyncNotCompletedException(String message) {
        super(message);
    }
}
