package com.scnsoft.eldermark.hl7v2.poll.http;

public class HttpPollingHl7GatewayException extends RuntimeException {

    public HttpPollingHl7GatewayException(String message) {
        super(message);
    }

    public HttpPollingHl7GatewayException(Throwable cause) {
        super(cause);
    }
}
