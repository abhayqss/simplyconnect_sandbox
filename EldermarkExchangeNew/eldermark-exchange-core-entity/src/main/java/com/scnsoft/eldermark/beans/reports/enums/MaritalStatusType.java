package com.scnsoft.eldermark.beans.reports.enums;

import java.util.stream.Stream;

public enum MaritalStatusType {

    SEPARATED("L", "Separated"),
    POLYGAMOUS("P", null),
    DIVORCED( "D", "Divorced"),
    DOMESTIC_PARTNER("T", "Living with partner"),
    MARRIED( "M", "Married"),
    WIDOWED("W", "Widow"),
    ANULLED( "A", null),
    NEVER_MARRIED("S", "Never married"),
    INTERLOCUTORY("I", null);

    MaritalStatusType(String ccdCode, String assessmentValue){
        this.ccdCode = ccdCode;
        this.assessmentValue = assessmentValue;
    };

    private String displayName;

    private String ccdCode;

    private String assessmentValue;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAssessmentValue() {
        return assessmentValue;
    }

    public void setAssessmentValue(String assessmentValue) {
        this.assessmentValue = assessmentValue;
    }

    public String getCcdCode() {
        return ccdCode;
    }

    public void setCcdCode(String ccdCode) {
        this.ccdCode = ccdCode;
    }

    public static MaritalStatusType fromAssessmentValue(String assessmentValue) {
        return Stream.of(MaritalStatusType.values()).filter(g -> assessmentValue.equals(g.getAssessmentValue())).findFirst().orElse(null);
    }
}
