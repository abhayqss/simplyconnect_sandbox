package com.scnsoft.eldermark.exception;

import org.springframework.security.authentication.AccountStatusException;

public class EmployeeConfirmedStatusException extends AccountStatusException {
    public EmployeeConfirmedStatusException(String msg) {
        super(msg);
    }

    public EmployeeConfirmedStatusException(String msg, Throwable t) {
        super(msg, t);
    }
}
