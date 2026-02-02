package com.scnsoft.eldermark.shared.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AccessToDirectMailboxDeniedException extends LocalizedException{
    public AccessToDirectMailboxDeniedException() {
    }

    public AccessToDirectMailboxDeniedException(Object... params) {
        super(params);
    }

    public AccessToDirectMailboxDeniedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "direct.messaging.send.error";
    }

}
