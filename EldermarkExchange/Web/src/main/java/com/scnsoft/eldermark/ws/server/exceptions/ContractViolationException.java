package com.scnsoft.eldermark.ws.server.exceptions;

/**
 * Indicates web service client errors related to web service contract violation (for example, missing parameters).
 */
public class ContractViolationException extends RuntimeException {
    public ContractViolationException() {
    }

    public ContractViolationException(String message) {
        super(message);
    }
}
