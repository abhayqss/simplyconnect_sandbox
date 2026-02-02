package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NHINException extends LocalizedException {
    public NHINException() {
    }

    public NHINException(Object... params) {
        super(params);
    }

    public NHINException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "gateway.connect.error";
    }
}
