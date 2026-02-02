package com.scnsoft.eldermark.beans.reports.model;

public class ComprehensiveReportRecord extends Report{

    private String communityName;

    private Long clientId;

    private String assessmentStatus;

    private String assessmentResponse;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getAssessmentStatus() {
        return assessmentStatus;
    }

    public void setAssessmentStatus(String assessmentStatus) {
        this.assessmentStatus = assessmentStatus;
    }

    public String getAssessmentResponse() {
        return assessmentResponse;
    }

    public void setAssessmentResponse(String assessmentResponse) {
        this.assessmentResponse = assessmentResponse;
    }

}
