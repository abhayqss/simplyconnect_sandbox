package com.scnsoft.eldermark.api.shared.exception;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 */
public class ValidationException extends PhrException {

    private List<String> validationErrors = new ArrayList<>();

    public ValidationException(String message) {
        super(message);
        super.code = CONSTRAINT_VIOLATION_CODE;
        super.httpStatus = HttpStatus.SC_BAD_REQUEST;
    }

    public void addValidationError(String error) {
        validationErrors.add(error);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

}