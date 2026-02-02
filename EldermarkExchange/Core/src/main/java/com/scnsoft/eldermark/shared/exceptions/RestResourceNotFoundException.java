package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by averazub on 3/25/2016.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RestResourceNotFoundException extends RuntimeException{
    public RestResourceNotFoundException(String message){
        super(message);
    }
}
