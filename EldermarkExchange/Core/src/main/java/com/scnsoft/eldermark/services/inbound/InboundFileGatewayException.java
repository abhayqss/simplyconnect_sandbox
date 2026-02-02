package com.scnsoft.eldermark.services.inbound;

public class InboundFileGatewayException extends RuntimeException {

    public InboundFileGatewayException() {
    }

    public InboundFileGatewayException(String message) {
        super(message);
    }

    public InboundFileGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public InboundFileGatewayException(Throwable cause) {
        super(cause);
    }

    public InboundFileGatewayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
