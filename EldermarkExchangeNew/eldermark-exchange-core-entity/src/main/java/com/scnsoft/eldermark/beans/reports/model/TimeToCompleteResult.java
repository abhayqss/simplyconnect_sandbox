package com.scnsoft.eldermark.beans.reports.model;

import java.time.Instant;

public class TimeToCompleteResult {

    private String communityName;
    private Long clientId;
    private String clientName;
    private Instant assessmentStartDate;
    private Instant assessmentEndDate;
    private String completedBy;
    private Long timeToComplete;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Instant getAssessmentStartDate() {
        return assessmentStartDate;
    }

    public void setAssessmentStartDate(Instant assessmentStartDate) {
        this.assessmentStartDate = assessmentStartDate;
    }

    public Instant getAssessmentEndDate() {
        return assessmentEndDate;
    }

    public void setAssessmentEndDate(Instant assessmentEndDate) {
        this.assessmentEndDate = assessmentEndDate;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public Long getTimeToComplete() {
        return timeToComplete;
    }

    public void setTimeToComplete(Long timeToComplete) {
        this.timeToComplete = timeToComplete;
    }
}
