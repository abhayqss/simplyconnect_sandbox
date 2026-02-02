package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SendMessageException extends LocalizedException {
    public SendMessageException() {
    }

    public SendMessageException(Object... params) {
        super(params);
    }

    public SendMessageException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "direct.messaging.send.error";
    }
}
