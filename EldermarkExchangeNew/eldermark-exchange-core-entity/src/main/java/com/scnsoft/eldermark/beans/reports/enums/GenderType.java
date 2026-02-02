package com.scnsoft.eldermark.beans.reports.enums;

import java.util.stream.Stream;

public enum GenderType {

    MALE("Male", "M", "Male", 1),
    FEMALE("Female", "F", "Female", 2),
//    TRANSGENDER_MALE_TO_FEMALE("Transgendered Male to Female", 3),
//    TRANSGENDER_FEMALE_TO_MALE("Transgendered Female to Male", 4),
    UNDEFINED("Other", "UN", null, 5),
    INFO_NOT_COLLECTED("Information not collected", null, "Unknown", 77);
//    INDIVIDUAL_REFUSED("Individual refused", 7),
//    INDIVIDUAL_DOES_NOT_KNOW("Individual does not know", 8);

    GenderType(String description, String ccdCode, String assessmentValue, Integer code){
        this.description = description;
        this.ccdCode = ccdCode;
        this.assessmentValue = assessmentValue;
        this.code = code;
    }

    private String description;

    private Integer code;

    private String ccdCode;

    private String assessmentValue;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

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

    public static GenderType fromCcdCode(String ccdCode) {
        return Stream.of(GenderType.values()).filter(g -> ccdCode.equals(g.getCcdCode())).findFirst().orElse(null);
    }

    public static GenderType fromAssessmentValue(String assessmentValue) {
        return Stream.of(GenderType.values()).filter(g -> assessmentValue.equals(g.getAssessmentValue())).findFirst().orElse(null);
    }
}
