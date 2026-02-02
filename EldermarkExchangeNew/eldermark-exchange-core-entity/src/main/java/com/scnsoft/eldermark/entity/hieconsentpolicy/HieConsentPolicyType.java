package com.scnsoft.eldermark.entity.hieconsentpolicy;

public enum HieConsentPolicyType {
    OPT_IN("Opted In", "Opt In"),
    OPT_OUT("Opted Out", "Opt Out");

    private final String displayName;
    private final String reportDisplayName;

    HieConsentPolicyType(String displayName, String reportDisplayName) {
        this.displayName = displayName;
        this.reportDisplayName = reportDisplayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getReportDisplayName() {
        return reportDisplayName;
    }
}
