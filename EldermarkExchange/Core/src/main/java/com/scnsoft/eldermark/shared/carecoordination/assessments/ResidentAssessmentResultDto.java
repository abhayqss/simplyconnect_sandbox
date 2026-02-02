package com.scnsoft.eldermark.shared.carecoordination.assessments;

import com.scnsoft.eldermark.entity.AssessmentStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ResidentAssessmentResultDto {
    private Long id;
    private Long assessmentId;
    private String comment;
    @DateTimeFormat(pattern = "MM/dd/yyyy hh:mm:ss a Z")
    private Date dateCompleted;
    @DateTimeFormat(pattern = "MM/dd/yyyy hh:mm:ss a Z")
    private Date dateAssigned;
    private Long employeeId;
    private Long patientId;
    private String employeeName;
    private String employeeNameAndRole;
    private String resultJson;
    private Long chainId;
    private Long score;
    private AssessmentStatus assessment_status;

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getEmployeeNameAndRole() {
        return employeeNameAndRole;
    }

    public void setEmployeeNameAndRole(String employeeNameAndRole) {
        this.employeeNameAndRole = employeeNameAndRole;
    }

    public AssessmentStatus getAssessment_status() {
        return assessment_status;
    }

    public void setAssessment_status(AssessmentStatus assessment_status) {
        this.assessment_status = assessment_status;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
}
