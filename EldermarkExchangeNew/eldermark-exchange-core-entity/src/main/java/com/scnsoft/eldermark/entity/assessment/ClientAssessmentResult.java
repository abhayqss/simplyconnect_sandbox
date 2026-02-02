package com.scnsoft.eldermark.entity.assessment;

import com.scnsoft.eldermark.beans.projection.AssessmentScoringCalculable;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.ClientAwareAuditableEntity;
import com.scnsoft.eldermark.entity.event.Event;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "ResidentAssessmentResult")
public class ClientAssessmentResult extends ClientAwareAuditableEntity implements Serializable, AssessmentScoringCalculable {

    private static final long serialVersionUID = 1L;

    @Column(name = "resident_id", insertable = false, updatable = false, nullable = false)
    private Long clientId;

    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Assessment assessment;

    @Column(name = "assessment_id", insertable = false, updatable = false, nullable = false)
    private Long assessmentId;

    @Column(name = "json_result")
    private String result;

    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private Employee employee;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @Column(name = "date_assigned", nullable = false)
    private Instant dateStarted;

    @Column(name = "date_completed", nullable = false)
    private Instant dateCompleted;

    @Column(name = "comment")
    private String comment;

    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_status", nullable = false)
    private AssessmentStatus assessmentStatus;

    @Column(name = "time_to_complete")
    private Long timeToComplete;

    @Column(name = "has_errors")
    private Boolean hasErrors;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ResidentAssessmentResult_ServicePlanNeedExcludedQuestion", joinColumns = @JoinColumn(name = "resident_assessment_result_id"))
    @Column(name = "question_name")
    private Set<String> servicePlanNeedIdentificationExcludedQuestions;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ResidentAssessmentResult_ServicePlanNeedExcludedSection", joinColumns = @JoinColumn(name = "resident_assessment_result_id"))
    @Column(name = "section_name")
    private Set<String> servicePlanNeedIdentificationExcludedSections;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
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

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Instant getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Instant dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Instant getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Instant dateCompleted) {
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

    public AssessmentStatus getAssessmentStatus() {
        return assessmentStatus;
    }

    public void setAssessmentStatus(AssessmentStatus assessmentStatus) {
        this.assessmentStatus = assessmentStatus;
    }

    public Long getTimeToComplete() {
        return timeToComplete;
    }

    public void setTimeToComplete(Long timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    public Boolean getHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(Boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Set<String> getServicePlanNeedIdentificationExcludedQuestions() {
        return servicePlanNeedIdentificationExcludedQuestions;
    }

    public void setServicePlanNeedIdentificationExcludedQuestions(Set<String> servicePlanNeedIdentificationExcludedQuestions) {
        this.servicePlanNeedIdentificationExcludedQuestions = servicePlanNeedIdentificationExcludedQuestions;
    }

    public Set<String> getServicePlanNeedIdentificationExcludedSections() {
        return servicePlanNeedIdentificationExcludedSections;
    }

    public void setServicePlanNeedIdentificationExcludedSections(Set<String> servicePlanNeedIdentificationExcludedSections) {
        this.servicePlanNeedIdentificationExcludedSections = servicePlanNeedIdentificationExcludedSections;
    }
}
