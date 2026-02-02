package com.scnsoft.eldermark.shared.carecoordination.assessments;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scnsoft.eldermark.shared.json.MeridianDateSerializer;

import java.util.Date;

public class AssessmentHistoryDto {

    private Long id;
    private Date date;
    private String status;
    private String author;
    private Long parentAssessment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date lastModifiedDate) {
        this.date = lastModifiedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getParentAssessment() {
        return parentAssessment;
    }

    public void setParentAssessment(Long parentAssessment) {
        this.parentAssessment = parentAssessment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
