package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DirectNotConfiguredException extends DirectMessagingException {
    public DirectNotConfiguredException() {
    }

    public DirectNotConfiguredException(String message) {
        super(message);
    }

    public DirectNotConfiguredException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.direct.messaging.not.configured";
    }
}
