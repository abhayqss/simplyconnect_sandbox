package com.scnsoft.eldermark.dump.entity.assessment;

import com.scnsoft.eldermark.dump.entity.ClientAwareAuditableEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ResidentAssessmentResult")
public class ClientAssessmentResult extends ClientAwareAuditableEntity {

    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Assessment assessment;

    @Column(name = "json_result")
    private String result;

    @Column(name = "date_assigned", nullable = false)
    private LocalDateTime dateAssigned;

    @Column(name = "date_completed", nullable = false)
    private LocalDateTime dateCompleted;

    @Column(name = "comment")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_status", nullable = false)
    private AssessmentStatus assessmentStatus;

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(LocalDateTime dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public LocalDateTime getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(LocalDateTime dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public AssessmentStatus getAssessmentStatus() {
        return assessmentStatus;
    }

    public void setAssessmentStatus(AssessmentStatus assessmentStatus) {
        this.assessmentStatus = assessmentStatus;
    }
}
