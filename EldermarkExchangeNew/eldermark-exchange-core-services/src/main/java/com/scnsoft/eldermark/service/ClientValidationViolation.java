package com.scnsoft.eldermark.service;

public enum ClientValidationViolation {
    COMMUNITY("Client community is not in organization."),
    EMAIL("Email should be unique within the organization."),
    MEDICARE_NUMBER("The client with the Medicaid Number entered already exists in the community. Medicaid Number must be unique within the community."),
    MEDICAID_NUMBER("The client with the Medicare Number entered already exists in the community. Medicare Number must be unique within the community."),
    SSN("SSN should be unique within the community."),
    SHARING("Sharing details not active at community level."),
    BIRTH_DATE("Birth date can not be in future.");

    private final String errorMessage;

    ClientValidationViolation(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public String getErrorMessage() {
        return errorMessage;
    }
}
