package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class ResidentOptedOutException extends LocalizedException {
    public ResidentOptedOutException() {
    }

    public ResidentOptedOutException(String message) {
        super(message);
    }

    public ResidentOptedOutException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.resident.opted.out";
    }
}
