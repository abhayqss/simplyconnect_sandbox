package com.scnsoft.eldermark.api.shared.web.dto;

/**
 * Created by averazub on 1/9/2017.
 */
public class ResponseErrorDto {
    protected String code;
    protected String message;

    public ResponseErrorDto() {
    }

    public ResponseErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
