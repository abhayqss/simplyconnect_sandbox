package com.scnsoft.eldermark.exception.integration.inbound.document;

public enum DocumentAssignmentErrorType {
    REQUIRED_PARAM_MISSING("Required parameter is missing."),
    RESIDENT_NOT_FOUND("The specified resident can not be found."),
    INTERNAL_ERROR("Internal API Error");

    private final String message;

    DocumentAssignmentErrorType(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
