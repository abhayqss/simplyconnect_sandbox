package com.scnsoft.eldermark.exception;

public class XdsCommunicationException extends InternalServerException {
    public XdsCommunicationException(Throwable cause) {
        super(InternalServerExceptionType.XDS_COMMUNICATION_FAILED, cause);
    }

    public XdsCommunicationException(String message) {
        super(InternalServerExceptionType.XDS_COMMUNICATION_FAILED, message);
    }
}
