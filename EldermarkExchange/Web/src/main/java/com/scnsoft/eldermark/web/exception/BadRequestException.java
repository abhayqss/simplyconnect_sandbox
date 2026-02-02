package com.scnsoft.eldermark.web.exception;

/**
 * Created by pzhurba on 10-Nov-15.
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
}
