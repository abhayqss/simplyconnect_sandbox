package com.scnsoft.eldermark.shared.carecoordination.assessments;

import java.util.Date;

public class ResidentAssessmentScoringDto {
    private Long id;
    private Long assessmentId;
    private String assessmentName;
    private String assessmentShortName;
    private Long assessmentScore;
    private String warning;
    private String severity;
    private String managementComment;
    private String severityColumnName;
    private Boolean isShouldSendEvents;
    private String severityShort;

    private Date dateCompleted;
    private String completedBy;
    private String comment;
    private String assessmentResult;
    private String assessmentContent;
    private Boolean scoringEnabled;
    private Boolean hasNumeration;

    private Boolean type;

    public String getSeverityShort() {
        return severityShort;
    }

    public void setSeverityShort(String severityShort) {
        this.severityShort = severityShort;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public Long getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(Long assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public String getAssessmentShortName() {
        return assessmentShortName;
    }

    public void setAssessmentShortName(String assessmentShortName) {
        this.assessmentShortName = assessmentShortName;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getManagementComment() {
        return managementComment;
    }

    public void setManagementComment(String managementComment) {
        this.managementComment = managementComment;
    }

    public String getSeverityColumnName() {
        return severityColumnName;
    }

    public void setSeverityColumnName(String severityColumnName) {
        this.severityColumnName = severityColumnName;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAssessmentResult() {
        return assessmentResult;
    }

    public void setAssessmentResult(String assessmentResult) {
        this.assessmentResult = assessmentResult;
    }

    public String getAssessmentContent() {
        return assessmentContent;
    }

    public void setAssessmentContent(String assessmentContent) {
        this.assessmentContent = assessmentContent;
    }

    public Boolean getScoringEnabled() {
        return scoringEnabled;
    }

    public void setScoringEnabled(Boolean scoringEnabled) {
        this.scoringEnabled = scoringEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getHasNumeration() {
        return hasNumeration;
    }

    public void setHasNumeration(Boolean hasNumeration) {
        this.hasNumeration = hasNumeration;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public Boolean getShouldSendEvents() {
        return isShouldSendEvents;
    }

    public void setShouldSendEvents(Boolean shouldSendEvents) {
        isShouldSendEvents = shouldSendEvents;
    }
}
