package com.scnsoft.eldermark.shared.web.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.exception.ValidationException;
import org.apache.http.HttpStatus;
import org.springframework.data.domain.Page;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.List;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 12/27/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    @XmlTransient
    public static Integer SUCCESS_STATUS_CODE = HttpStatus.SC_OK;


    @XmlTransient
    public static <T> Response<T> successResponse() {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, null);
        return response;
    }

    @XmlTransient
    public static <T> Response<T> successResponse(T data) {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, data);
        return response;
    }

    @XmlTransient
    public static <T> Response<T> successResponse(T data, Boolean editable) {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, data, editable);
        return response;
    }

    @XmlTransient
    public static <T> Response<T> successResponse(T data, Date minimumDate) {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, data, minimumDate, null);
        return response;
    }

    @XmlTransient
    public static <T> Response<T> pagedResponse(T data, Long totalCount) {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, data, null, totalCount);
        return response;
    }

    @XmlTransient
    public static <E> Response<List<E>> pagedResponse(Page<E> page) {
        Response<List<E>> response = new Response<>(SUCCESS_STATUS_CODE, true, null, page.getContent(), null, page.getTotalElements());
        return response;
    }

    @XmlTransient
    public static <T> Response<T> pagedResponse(T data, Long totalCount, Date minimumDate) {
        Response<T> response = new Response<>(SUCCESS_STATUS_CODE, true, null, data, minimumDate, totalCount);
        return response;
    }

    @XmlTransient
    public static <T> Response<T> errorResponse (String error, int statusCode) {
        Response<T> response = new Response<>(statusCode, false,
                new PhrException(statusCode == HttpStatus.SC_NOT_FOUND ? PhrException.NOT_FOUND_CODE : PhrException.INTERNAL_SERVER_ERROR_CODE, error), null);
        return response;
    }

    @XmlTransient
    public static <T> Response<T> errorResponse (PhrException e, int statusCode) {
        Response<T> response = new Response<>(statusCode, false, e, null);
        return response;
    }

    @XmlTransient
    public static <T> Response<T> errorResponse (PhrExceptionType errorType) {
        Response<T> response = new Response<>(errorType.httpStatus(), false, new PhrException(errorType), null);
        return response;
    }

    public Response() {
    }

    public Response(int statusCode, Boolean success, PhrException error, T data) {
        this.statusCode = statusCode;
        body = new ResponseBody<>(success, error, data);
    }

    public Response(int statusCode, Boolean success, PhrException error, T data, Boolean editable) {
        this.statusCode = statusCode;
        body = new ResponseBody<>(success, error, data, editable);
    }

    public Response(int statusCode, Boolean success, PhrException error, T data, Date minimumDate, Long totalCount) {
        this.statusCode = statusCode;
        body = new ResponseBody<>(success, error, data, minimumDate, totalCount);
    }

    private ResponseBody<T> body;
    private Integer statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseBody<T> {
        private final Boolean editable;
        private final Date minimumDate;
        private final Long totalCount;
        private Boolean success;
        private ResponseErrorDto error;
        private T data;

        public ResponseBody(T data) {
            this.editable = null;
            this.minimumDate = null;
            this.totalCount = null;
            this.success = true;
            this.error = null;
            this.data = data;
        }

        public ResponseBody(PhrException error) {
            this.editable = null;
            this.minimumDate = null;
            this.totalCount = null;
            this.success = false;
            this.error = error==null ? null : new ResponseErrorDto(error.getCode(), error.getMessage());
            this.data = null;
        }

        public ResponseBody(Boolean success, PhrException error, T data) {
            this.editable = null;
            this.minimumDate = null;
            this.totalCount = null;
            this.success = success;
            if (error == null) {
                this.error = null;
            } else if (error instanceof ValidationException) {
                this.error = new ResponseValidationErrorDto(error.getCode(), error.getMessage(), ((ValidationException) error).getValidationErrors());
            } else {
                this.error = new ResponseErrorDto(error.getCode(), error.getMessage());
            }
            this.data = data;
        }

        public ResponseBody(Boolean success, PhrException error, T data, Boolean editable) {
            this.editable = editable;
            this.minimumDate = null;
            this.totalCount = null;
            this.success = success;
            this.error = error==null ? null : new ResponseErrorDto(error.getCode(), error.getMessage());
            this.data = data;
        }

        public ResponseBody(Boolean success, PhrException error, T data, Date minimumDate, Long totalCount) {
            this.editable = null;
            this.minimumDate = minimumDate;
            this.totalCount = totalCount;
            this.success = success;
            this.error = error==null ? null : new ResponseErrorDto(error.getCode(), error.getMessage());
            this.data = data;
        }

        public Boolean getEditable() {
            return editable;
        }

        public Date getMinimumDate() {
            return minimumDate;
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
