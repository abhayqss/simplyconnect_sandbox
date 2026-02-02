package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DocumentNotFoundException extends LocalizedException {
    public DocumentNotFoundException() {
    }

    public DocumentNotFoundException(Object ...params) {
        super(params);
    }

    public DocumentNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.document.not.found";
    }
}
