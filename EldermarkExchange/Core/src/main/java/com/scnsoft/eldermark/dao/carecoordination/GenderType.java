package com.scnsoft.eldermark.dao.carecoordination;

public enum GenderType {

    MALE("M", "Male"),
    FEMALE("F", "Female"),
    UNDEFINED("UN", "Unknown");

    GenderType(String ccdCode, String assessmentValue){
        this.ccdCode = ccdCode;
        this.assessmentValue = assessmentValue;
    }

    private String ccdCode;

    private String assessmentValue;

    public String getCcdCode() {
        return ccdCode;
    }

    public void setCcdCode(String ccdCode) {
        this.ccdCode = ccdCode;
    }

    public String getAssessmentValue() {
        return assessmentValue;
    }

    public void setAssessmentValue(String assessmentValue) {
        this.assessmentValue = assessmentValue;
    }

    public static GenderType fromAssessmentValue(String assessmentValue) {
        for (GenderType genderType : GenderType.values()){
            if (genderType.getAssessmentValue().equals(assessmentValue)){
                return genderType;
            }
        }
        return null;
    }

}
