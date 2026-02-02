package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DirectAccountDuplicatedException extends DirectMessagingException {
    public DirectAccountDuplicatedException() {
    }

    public DirectAccountDuplicatedException(String message) {
        super(message);
    }

    public DirectAccountDuplicatedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.secure.email.duplicated";
    }
}
