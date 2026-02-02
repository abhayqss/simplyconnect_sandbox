package com.scnsoft.eldermark.dao.exceptions;

/**
 * Created by pzhurba on 05-Nov-15.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email){
        super("User with email " + email + " already exists");
    }
}
