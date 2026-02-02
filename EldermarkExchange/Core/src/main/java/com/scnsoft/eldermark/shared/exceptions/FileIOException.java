package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileIOException extends LocalizedException {
    public FileIOException() {
    }

    public FileIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileIOException(Object... params) {
        super(params);
    }

    public FileIOException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.file.io";
    }
}
