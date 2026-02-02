package com.scnsoft.eldermark.shared.web.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author phomal
 */
public class ResponseValidationErrorDto extends ResponseErrorDto {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> validationErrors;

    public ResponseValidationErrorDto() {
    }

    public ResponseValidationErrorDto(String code, String message, List<String> validationErrors) {
        super(code, message);
        this.validationErrors = validationErrors;
    }

    @ApiModelProperty(value = "A list of validation error messages")
    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
