package com.scnsoft.eldermark.shared.carecoordination.assessments;


public class ResidentAssessmentPriorityCheckDto {

    private String assessmentContent;
    private String assessmentName;
    private Boolean hasNumeration;

    public String getAssessmentContent() {
        return assessmentContent;
    }

    public void setAssessmentContent(String assessmentContent) {
        this.assessmentContent = assessmentContent;
    }

    public Boolean getHasNumeration() {
        return hasNumeration;
    }

    public void setHasNumeration(Boolean hasNumeration) {
        this.hasNumeration = hasNumeration;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }
}
