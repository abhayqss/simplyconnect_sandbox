package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CdaViewGenerationException extends LocalizedException {

    public CdaViewGenerationException() {
        super();
    }

    public CdaViewGenerationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "cda.view.generation.failure";
    }
}
