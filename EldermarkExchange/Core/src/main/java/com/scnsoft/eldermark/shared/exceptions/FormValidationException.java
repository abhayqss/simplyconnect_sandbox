package com.scnsoft.eldermark.shared.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FormValidationException extends LocalizedException {
    @Override
    public String getCode() {
        return "error.form.is.not.valid";
    }
}
