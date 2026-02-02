package com.scnsoft.eldermark.exception;

public class AuthAccountInactiveException extends AuthException {
    public AuthAccountInactiveException() {
        super(InternalServerExceptionType.AUTH_ACCOUNT_INACTIVE);
    }
}
