package com.scnsoft.eldermark.entity.signature;

public enum TemplateFieldDefaultValueType {
    CLIENT_GENDER(true),
    CLIENT_UNIT_NUMBER(true),
    CLIENT_BIRTH_DATE(true),
    CLIENT_MEDICARE_SSN_NUMBER(true),
    CLIENT_INSURANCE_NAME(true),
    CLIENT_INSURANCE_PLAN(true),
    CLIENT_INSURANCE_PHONE(true),
    CLIENT_INSURANCE_MEMBER_NUMBER(true),
    CLIENT_INSURANCE_GROUP_NUMBER(true),
    CLIENT_EMAIL(true),
    CLIENT_ADDRESS(true),
    CLIENT_FULL_ADDRESS(true),
    CLIENT_CITY(true),
    CLIENT_STATE(true),
    CLIENT_CITY_AND_STATE(true),
    CLIENT_ZIP(true),
    CLIENT_CELL_PHONE(true),
    CLIENT_HOME_PHONE(true),
    CLIENT_HOME_OR_CELL_PHONE(true),
    CLIENT_ALLERGIES(true),
    CLIENT_ALLERGIES_REACTIONS(true),
    CLIENT_ACTIVE_DIAGNOSES(true),
    CURRENT_DATE(false),
    CURRENT_DAY(false),
    CURRENT_MONTH(false),
    CURRENT_YEAR(false),
    CURRENT_USER_NAME(false),
    CURRENT_USER_ROLE(false),
    CURRENT_USER_CELL_PHONE(false),
    CURRENT_USER_EMAIL(false),
    CLIENT_NAME(true),
    CLIENT_FULL_NAME(true),
    CLIENT_MIDDLE_NAME(true),
    CLIENT_LAST_NAME(true),
    CLIENT_FIRST_NAME(true),
    CLIENT_COMMUNITY_NAME(true),
    CLIENT_COMMUNITY_AND_UNIT_NUMBER(true),
    CLIENT_COMMUNITY_ADDRESS(true),
    CLIENT_MEDICARE_NUMBER(true),
    CLIENT_MEDICAID_NUMBER(true),
    CLIENT_SSN_LAST_FOUR_DIGITS(true),
    CLIENT_COMMUNITY_PHONE(true);

    private final boolean isClientField;

    TemplateFieldDefaultValueType(boolean isClientField) {
        this.isClientField = isClientField;
    }

    public boolean getIsClientField() {
        return isClientField;
    }
}
