package com.scnsoft.eldermark.shared.exception;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * A simple helper to create ValidationException from BindingResult or java Form Input Validation result
 * @author phomal
 */
public class ValidationExceptionFactory {

    public static ValidationException fromBindingErrors(Errors errors) {
        ValidationException error = new ValidationException(errors.getErrorCount() < 2 ? "" : "Validation failed. " + errors.getErrorCount() + " errors");
        for (ObjectError objectError : errors.getAllErrors()) {
            if (objectError instanceof FieldError) {
                error.addValidationError(((FieldError) objectError).getField() + " " + objectError.getDefaultMessage());
            } else {
                error.addValidationError(objectError.getDefaultMessage());
            }
        }
        return error;
    }

    public static ValidationException fromConstraintViolation(ConstraintViolationException exc, Boolean withPropertyPath) {
        final int count = exc.getConstraintViolations().size();
        ValidationException error = new ValidationException(count < 2 ? "" : "Validation failed. " + count + " errors");
        for (ConstraintViolation<?> violation : exc.getConstraintViolations()) {
            error.addValidationError((withPropertyPath ? violation.getPropertyPath().toString() + " " : "") +  violation.getMessage());
        }
        return error;
    }

}