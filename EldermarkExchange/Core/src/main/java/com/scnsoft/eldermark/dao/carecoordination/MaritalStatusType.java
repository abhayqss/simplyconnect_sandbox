package com.scnsoft.eldermark.dao.carecoordination;

public enum  MaritalStatusType {

    SEPARATED("L", "Separated"),
    POLYGAMOUS("P", null),
    DIVORCED("D", "Divorced"),
    DOMESTIC_PARTNER("T", "Living with partner"),
    MARRIED("M", "Married"),
    WIDOWED("W", "Widow"),
    ANULLED( "A", null),
    NEVER_MARRIED( "S", "Never married"),
    INTERLOCUTORY("I", null);

    MaritalStatusType(String ccdCode, String assessmentValue){
        this.ccdCode = ccdCode;
        this.assessmentValue = assessmentValue;
    };

    private String displayName;

    private String ccdCode;

    private String assessmentValue;

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
        for (MaritalStatusType statusType : MaritalStatusType.values()){
            if (statusType.assessmentValue != null && statusType.assessmentValue.equals(assessmentValue)){
                return statusType;
            }
        }
        return null;
    }

}
