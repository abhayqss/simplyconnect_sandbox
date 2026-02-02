package com.scnsoft.eldermark.entity.password;

import java.util.Optional;

public enum PasswordSettingsType {
    PASSWORD_MAXIMUM_AGE_IN_DAYS("Password", false, null),
    ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT("Account", false, null),
    ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES("Account", false, null),
    ACCOUNT_LOCK_IN_MINUTES("Account", false, null),

    COMPLEXITY_PASSWORD_LENGTH("Complexity", true, ".{%d,}") {
        @Override
        public String getDisplayName(Long value) {
            return value + " characters minimum";
        }
    },

    COMPLEXITY_ALPHABETIC_COUNT("Complexity", false, "(?=.*[a-zA-Z]{%d,}).*") {
        @Override
        public String getDisplayName(Long value) {
            return value + " alphabetic characters minimum";
        }
    },

    COMPLEXITY_UPPERCASE_COUNT("Complexity", true, "(?=.*[A-Z]{%d,}).*") {
        @Override
        public String getDisplayName(Long value) {
            return value + " uppercase character(s)";
        }
    },

    COMPLEXITY_LOWERCASE_COUNT("Complexity", true, "(?=.*[a-z]{%d,}).*") {
        @Override
        public String getDisplayName(Long value) {
            return value + " lowercase character(s)";
        }
    },

    COMPLEXITY_ARABIC_NUMERALS_COUNT("Complexity", true, "(?=.*\\d{%d,}).*") {
        @Override
        public String getDisplayName(Long value) {
            return value + " number(s)";
        }
    },

    COMPLEXITY_NON_ALPHANUMERIC_COUNT("Complexity", false, "(?=.*[!\"#$%%&'()*+,\\-.\\/:;<=>?@\\[\\\\\\]^_`{|}~]{%d,}).*") {
        @Override
        public String getDisplayName(Long value) {
            return value + " special symbol(s) (e.g. @#$%!)";
        }
    },

    COMPLEXITY_LESS_SPACES_THAN("Complexity", false, "^(?!.*\\s{1,}).*$") {
        @Override
        public String getDisplayName(Long value) {
            if (value > 1) {
                return "Up to " + (value - 1) + " spaces";
            }
            return "No spaces";
        }
    },
    COMPLEXITY_PASSWORD_HISTORY_COUNT("Complexity", false, null);

    PasswordSettingsType(String section, Boolean mandatory, String regexpPattern) {
        this.section = section;
        this.mandatory = mandatory;
        this.regexpPattern = regexpPattern;
    }

    private final String section;
    private final Boolean mandatory;
    private final String regexpPattern;

    public String getSection() {
        return section;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public Optional<String> getRegexp(Long value) {
        return Optional.ofNullable(regexpPattern).map(p -> String.format(p, value));
    }

    public String getDisplayName(Long value) {
        return "";
    }

    public boolean hasRegexp() {
        return regexpPattern != null;
    }
}
