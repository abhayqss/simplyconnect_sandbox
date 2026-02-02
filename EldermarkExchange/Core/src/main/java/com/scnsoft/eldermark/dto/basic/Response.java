package com.scnsoft.eldermark.dto.basic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.exception.WebApplicationException;
import com.scnsoft.eldermark.exception.WebApplicationExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;

import org.springframework.http.HttpStatus;

/**
 * @author averazub
 * @author phomal Created by averazub on 12/27/2016.
 */

public class Response<T> {

    public static Integer SUCCESS_STATUS_CODE = HttpStatus.OK.value();

    public static Integer INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public static Integer NOT_FOUND = HttpStatus.NOT_FOUND.value();

    public static <T> Response<T> successResponse() {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, null);
        return response;
    }

    public static <T> Response<T> successResponse(T data) {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, data);
        return response;
    }

    public static <T> Response<T> pagedResponse(T data, Long totalCount) {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, data, totalCount);
        return response;
    }

    public static <T> Response<T> errorResponse(String error, int statusCode) {
        Response<T> response = new Response<>(statusCode, false,
                new WebApplicationException(statusCode == NOT_FOUND ? WebApplicationException.NOT_FOUND_CODE
                        : WebApplicationException.INTERNAL_SERVER_ERROR_CODE, error),
                null);
        return response;
    }

    public static <T> Response<T> errorResponse(WebApplicationException e, int statusCode) {
        Response<T> response = new Response<>(statusCode, false, e, null);
        return response;
    }

    public static <T> Response<T> errorResponse(WebApplicationExceptionType errorType) {
        Response<T> response = new Response<>(errorType.httpStatus(), false, new WebApplicationException(errorType), null);
        return response;
    }

    public Response() {
    }

    public Response(int statusCode, Boolean success, WebApplicationException error, T data) {
        this.statusCode = statusCode;
        body = new ResponseBody<>(success, error, data);
    }

    public Response(int statusCode, Boolean success, WebApplicationException error, T data, Long totalCount) {
        this.statusCode = statusCode;
        body = new ResponseBody<>(totalCount, success, error, data);
    }

    private ResponseBody<T> body;
    private Integer statusCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseBody<T> {
        private final Long totalCount;
        private Boolean success;
        private ResponseErrorDto error;
        private T data;

        public ResponseBody(T data) {
            this.totalCount = null;
            this.success = true;
            this.error = null;
            this.data = data;
        }

        public ResponseBody(Long totalCount, Boolean success, WebApplicationException error, T data) {
            super();
            this.totalCount = totalCount;
            this.success = success;
            this.error = error == null ? null : new ResponseErrorDto(error.getCode(), error.getMessage());
            this.data = data;
        }

        public ResponseBody(WebApplicationException error) {
            this.totalCount = null;
            this.success = false;
            this.error = error == null ? null : new ResponseErrorDto(error.getCode(), error.getMessage());
            this.data = null;
        }

        public ResponseBody(Boolean success, WebApplicationException error, T data) {
            this.totalCount = null;
            this.success = success;
            if (error == null) {
                this.error = null;
            } else if (error instanceof ValidationException) {
                this.error = new ResponseValidationErrorDto(error.getCode(), error.getMessage(),
                        ((ValidationException) error).getValidationErrors());
            } else {
                this.error = new ResponseErrorDto(error.getCode(), error.getMessage());
            }
            this.data = data;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public ResponseErrorDto getError() {
            return error;
        }

        public void setError(ResponseErrorDto error) {
            this.error = error;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    public ResponseBody<T> getBody() {
        return body;
    }

    public void setBody(ResponseBody<T> body) {
        this.body = body;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
