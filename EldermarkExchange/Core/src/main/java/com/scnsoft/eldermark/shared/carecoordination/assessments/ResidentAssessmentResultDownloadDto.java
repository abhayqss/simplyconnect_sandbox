package com.scnsoft.eldermark.shared.carecoordination.assessments;

import com.fasterxml.jackson.annotation.JsonRawValue;

public class ResidentAssessmentResultDownloadDto {

    private Long residentId;

    @JsonRawValue
    private String assessmentData;


    public ResidentAssessmentResultDownloadDto() {
    }

    public ResidentAssessmentResultDownloadDto(Long residentId, String assessmentData) {
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
