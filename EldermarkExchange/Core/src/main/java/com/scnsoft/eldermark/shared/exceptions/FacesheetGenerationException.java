package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FacesheetGenerationException extends LocalizedException {

    @Override
    public String getCode() {
        return "facesheet.generation.failure";
    }
}
