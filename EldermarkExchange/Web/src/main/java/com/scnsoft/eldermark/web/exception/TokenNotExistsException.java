package com.scnsoft.eldermark.web.exception;

/**
 * Created by pzhurba on 04-Nov-15.
 */
public class TokenNotExistsException extends RuntimeException {
    public TokenNotExistsException(final String message){
        super(message);
    }
}
