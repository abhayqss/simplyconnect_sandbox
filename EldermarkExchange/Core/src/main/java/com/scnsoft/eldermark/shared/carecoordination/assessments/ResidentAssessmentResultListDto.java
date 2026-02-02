package com.scnsoft.eldermark.shared.carecoordination.assessments;

import java.util.Date;

public class ResidentAssessmentResultListDto {
    private Long id;
    private String assessmentName;
    private String status;
    private Date dateAssigned;
    private Date dateCompleted;
    private String author;
    private Boolean editable = false;
    private Boolean canBeDownloaded = false;
    private Long assessmentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getCanBeDownloaded() {
        return canBeDownloaded;
    }

    public void setCanBeDownloaded(Boolean canBeDownloaded) {
        this.canBeDownloaded = canBeDownloaded;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }
}
