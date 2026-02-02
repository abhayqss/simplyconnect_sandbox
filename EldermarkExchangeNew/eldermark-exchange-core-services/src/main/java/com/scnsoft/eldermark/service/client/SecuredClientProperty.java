package com.scnsoft.eldermark.service.client;

public enum SecuredClientProperty {
    BIRTH_DATE("birthDate"),
    SSN("ssn"),
    SSN_LAST_FOUR_DIGITS("ssnLastFourDigits"),
    RISK_SCORE("riskScore");

    private final String name;

    SecuredClientProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
