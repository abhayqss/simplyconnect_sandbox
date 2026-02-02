package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResidentNotFoundException extends LocalizedException {
    public ResidentNotFoundException() {
    }

    public ResidentNotFoundException(String message) {
        super(message);
    }

    public ResidentNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.resident.not.found";
    }
}
