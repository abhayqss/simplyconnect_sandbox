package com.scnsoft.eldermark.cda.service.schema.enums;

public enum ValueSetEnum {

    PROBLEM_ACT_CODE_SYSTEM("2.16.840.1.113883.5.14", "v3 Code System ActStatus", "2.16.840.1.113883.11.20.9.19");

    private final String codeSystem;
    private final String codeSystemName;
    private final String valueSet;

    ValueSetEnum(final String codeSystem, final String codeSystemName, final String valueSet) {
        this.codeSystem = codeSystem;
        this.codeSystemName = codeSystemName;
        this.valueSet = valueSet;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public String getValueSet() {
        return valueSet;
    }
}
