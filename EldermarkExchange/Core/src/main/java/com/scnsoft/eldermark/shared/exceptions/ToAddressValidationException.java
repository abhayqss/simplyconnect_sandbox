package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ToAddressValidationException extends LocalizedException {
    public ToAddressValidationException() {
    }

    public ToAddressValidationException(Object... params) {
        super(params);
    }

    public ToAddressValidationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "direct.messaging.invalid.recipient";
    }
}
