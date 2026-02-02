package com.scnsoft.eldermark.validation;

public interface ValidationRegExpConstants {

    String EMPTY_OR = "(^$)|";

    String EMAIL_REGEXP = EMPTY_OR + "((?i)^[a-z0-9!#$%&'*+\\\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\\\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z]{2,}$)";
    String PHONE_REGEXP = EMPTY_OR + "(\\+?\\d{10,16})";
    String ZIP_CODE_REGEXP = EMPTY_OR + "(\\d{5})";
    String SSN_REGEXP = EMPTY_OR + "(\\d{9})";
}
