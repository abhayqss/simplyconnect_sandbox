package com.scnsoft.eldermark.dto.basic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseValidationErrorDto extends ResponseErrorDto {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> validationErrors;

    public ResponseValidationErrorDto() {
    }

    public ResponseValidationErrorDto(String code, String message, List<String> validationErrors) {
        super(code, message);
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
