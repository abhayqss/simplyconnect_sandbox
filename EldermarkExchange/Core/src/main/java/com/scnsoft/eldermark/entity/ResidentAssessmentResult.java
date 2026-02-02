package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ResidentAssessmentResult")
public class ResidentAssessmentResult extends ResidentAwareAuditableEntity implements Serializable {

    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Assessment assessment;

    @Column(name="json_result")
    private String result;

    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private Employee employee;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_assigned", nullable = false)
    private Date dateAssigned;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_completed", nullable = false)
    private Date dateCompleted;

    @Column(name="comment")
    private String comment;

    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @OneToOne
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name="assessment_status", nullable = false)
    private AssessmentStatus assessment_status;

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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public AssessmentStatus getAssessment_status() {
        return assessment_status;
    }

    public void setAssessment_status(AssessmentStatus assessment_status) {
        this.assessment_status = assessment_status;
    }
}
