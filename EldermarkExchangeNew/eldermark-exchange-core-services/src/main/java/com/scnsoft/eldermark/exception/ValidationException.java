package com.scnsoft.eldermark.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ValidationException extends BusinessException {

    private List<String> validationErrors = new ArrayList<>();

    public ValidationException(String message) {
        super(message);
        super.code = CONSTRAINT_VIOLATION_CODE;
        super.httpStatus = HttpStatus.BAD_REQUEST.value();
    }

    public void addValidationError(String error) {
        validationErrors.add(error);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
