package com.scnsoft.scansol.shared.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scnsoft.scansol.shared.enums.ERROR_CODE;
import com.scnsoft.scansol.shared.enums.STATUS;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ScanSolResponseBase {
    private Integer statusCode;

    private STATUS success;

    private String message;

    private ERROR_CODE errorCode;

    public ScanSolResponseBase () {
        this (STATUS.SUCCESS);
    }

    public ScanSolResponseBase (final STATUS success) {
        this.success = success;
        this.statusCode = success.ordinal ();
    }

    public ScanSolResponseBase (final STATUS success, final String message) {
        this (success);

        this.message = message;
    }

    public ScanSolResponseBase (final STATUS success, final ERROR_CODE errorCode, final String message) {
        this (success, message);

        this.errorCode = errorCode;
    }

    public STATUS getSuccess () {
        return success;
    }

    public void setSuccess (STATUS success) {
        this.success = success;
        this.statusCode = success.ordinal ();
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public Integer getStatusCode () {
        return statusCode;
    }

    public void setStatusCode (Integer statusCode) {
        this.statusCode = statusCode;
    }

    public ERROR_CODE getErrorCode () {
        return errorCode;
    }

    public void setErrorCode (ERROR_CODE errorCode) {
        this.errorCode = errorCode;
    }
}
