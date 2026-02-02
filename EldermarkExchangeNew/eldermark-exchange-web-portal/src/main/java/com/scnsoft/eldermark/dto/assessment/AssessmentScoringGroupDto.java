package com.scnsoft.eldermark.dto.assessment;

public class AssessmentScoringGroupDto {
    private Long scoreLow;
    private Long scoreHigh;
    private String severityShort;
    private String severity;
    private String highlighting;
    private String comments;
    private Boolean isRiskIdentified;

    public Long getScoreLow() {
        return scoreLow;
    }

    public void setScoreLow(Long scoreLow) {
        this.scoreLow = scoreLow;
    }

    public Long getScoreHigh() {
        return scoreHigh;
    }

    public void setScoreHigh(Long scoreHigh) {
        this.scoreHigh = scoreHigh;
    }

    public String getSeverityShort() {
        return severityShort;
    }

    public void setSeverityShort(String severityShort) {
        this.severityShort = severityShort;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getHighlighting() {
        return highlighting;
    }

    public void setHighlighting(String highlighting) {
        this.highlighting = highlighting;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getIsRiskIdentified() {
        return isRiskIdentified;
    }

    public void setIsRiskIdentified(Boolean riskIdentified) {
        isRiskIdentified = riskIdentified;
    }
}
