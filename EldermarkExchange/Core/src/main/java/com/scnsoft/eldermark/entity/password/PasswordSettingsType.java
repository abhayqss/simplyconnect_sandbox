package com.scnsoft.eldermark.entity.password;

public enum PasswordSettingsType {
    PASSWORD_MAXIMUM_AGE_IN_DAYS("Password", false),
    ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT("Account", false),
    ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES("Account", false),
    ACCOUNT_LOCK_IN_MINUTES("Account", false),
    COMPLEXITY_PASSWORD_LENGTH("Complexity", true),
    COMPLEXITY_ALPHABETIC_COUNT("Complexity", false),
    COMPLEXITY_UPPERCASE_COUNT("Complexity", true),
    COMPLEXITY_LOWERCASE_COUNT("Complexity", true),
    COMPLEXITY_ARABIC_NUMERALS_COUNT("Complexity", true),
    COMPLEXITY_NON_ALPHANUMERIC_COUNT("Complexity", false),
    COMPLEXITY_PASSWORD_HISTORY_COUNT("Complexity", false),
    COMPLEXITY_LESS_SPACES_THAN("Complexity", false);

    PasswordSettingsType(String section, Boolean mandatory) {
        this.section = section;
        this.mandatory = mandatory;
    }

    private String section;
    private Boolean mandatory;

    public String getSection() {
        return section;
    }

    public Boolean getMandatory() {
        return mandatory;
    }
}
