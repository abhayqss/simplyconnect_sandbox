package com.scnsoft.eldermark.exception;

public class SftpGatewayException extends InternalServerException {

    public SftpGatewayException(Throwable cause) {
        super(InternalServerExceptionType.SFTP_COMMUNICATION_FAILED, cause);
    }

}
