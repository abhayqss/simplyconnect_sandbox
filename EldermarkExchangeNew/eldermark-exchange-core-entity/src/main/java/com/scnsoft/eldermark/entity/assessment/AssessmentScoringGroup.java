package com.scnsoft.eldermark.entity.assessment;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AssessmentScoringGroup")
public class AssessmentScoringGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Assessment assessment;

    @Column(name="score_low")
    private Long scoreLow;

    @Column(name="score_high")
    private Long scoreHigh;

    @Column(name="severity")
    private String severity;

    @Column(name="severity_short")
    private String severityShort;

    @Column(name="highlighting")
    private String highlighting;

    @Column(name="comments")
    private String comments;

    @Column(name = "is_risk_identified")
    private Boolean isRiskIdentified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSeverityShort() {
        return severityShort;
    }

    public void setSeverityShort(String severityShort) {
        this.severityShort = severityShort;
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
