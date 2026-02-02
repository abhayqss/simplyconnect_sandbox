package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DirectAccountNotRegisteredException extends DirectMessagingException {
    public DirectAccountNotRegisteredException() {
    }

    public DirectAccountNotRegisteredException(Object... params) {
        super(params);
    }

    public DirectAccountNotRegisteredException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.secure.email.not.registered.yet";
    }
}
