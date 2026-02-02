package com.scnsoft.eldermark.shared.carecoordination.assessments;

public class SelectedAssessmentDto {
    private Long assessmentId;
    private Long patientId;

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
}
