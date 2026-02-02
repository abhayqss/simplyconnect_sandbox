package com.scnsoft.eldermark.dao.exceptions;

public class MultipleEntitiesFoundException extends RuntimeException {

    public MultipleEntitiesFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MultipleEntitiesFoundException(String message) {
        super(message);
    }
}
