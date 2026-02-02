package com.scnsoft.exchange.adt.entity;

/**
 * Created by averazub on 10/5/2016.
 */
public class ResponseDto {
    Integer statusCode;
    String status;
    String responseBody;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
