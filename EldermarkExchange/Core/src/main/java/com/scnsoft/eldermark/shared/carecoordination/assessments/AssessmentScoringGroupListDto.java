package com.scnsoft.eldermark.shared.carecoordination.assessments;

public class AssessmentScoringGroupListDto {
    private String score;
    private String severity;
    private String severityShort;
    private String comments;
    private Boolean passedHighEducation;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSeverityShort() {
        return severityShort;
    }

    public void setSeverityShort(String severityShort) {
        this.severityShort = severityShort;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getPassedHighEducation() {
        return passedHighEducation;
    }

    public void setPassedHighEducation(Boolean passedHighEducation) {
        this.passedHighEducation = passedHighEducation;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
