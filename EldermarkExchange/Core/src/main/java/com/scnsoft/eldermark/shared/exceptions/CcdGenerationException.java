package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CcdGenerationException extends LocalizedException {

    public CcdGenerationException() {
        super();
    }

    public CcdGenerationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "ccd.generation.failure";
    }
}
