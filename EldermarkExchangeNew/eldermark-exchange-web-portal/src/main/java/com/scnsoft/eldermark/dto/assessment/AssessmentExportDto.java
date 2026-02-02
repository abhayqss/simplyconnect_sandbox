package com.scnsoft.eldermark.dto.assessment;

import com.fasterxml.jackson.annotation.JsonRawValue;

public class AssessmentExportDto {

    private Long residentId;

    @JsonRawValue
    private String assessmentData;

    public AssessmentExportDto() {
    }

    public AssessmentExportDto(Long residentId, String assessmentData) {
        this.residentId = residentId;
        this.assessmentData = assessmentData;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getAssessmentData() {
        return assessmentData;
    }

    public void setAssessmentData(String assessmentData) {
        this.assessmentData = assessmentData;
    }
}
