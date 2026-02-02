package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DirectMessagingException extends LocalizedException {
    public DirectMessagingException() {
    }

    public DirectMessagingException(String message) {
        super(message);
    }

    public DirectMessagingException(Object... params) {
        super(params);
    }

    public DirectMessagingException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "direct.messaging.error";
    }
}
