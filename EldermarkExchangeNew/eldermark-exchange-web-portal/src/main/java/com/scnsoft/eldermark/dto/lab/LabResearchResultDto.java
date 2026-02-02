package com.scnsoft.eldermark.dto.lab;

import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;

import java.util.List;

public class LabResearchResultDto {

    private List<LabResearchResultDocumentDto> documents;
    private String source;
    private List<Long> dates;
    private String statusName;
    private String statusTitle;
    private String performerName;
    private String performerAddress;
    private String medicalDirector;

    private String commentSource;
    private List<String> comments;

    private CECodedElementDto specimenType;
    private Long specimenDate;
    private Long specimenReceivedDate;

    public List<LabResearchResultDocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<LabResearchResultDocumentDto> documents) {
        this.documents = documents;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Long> getDates() {
        return dates;
    }

    public void setDates(List<Long> dates) {
        this.dates = dates;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getPerformerName() {
        return performerName;
    }

    public void setPerformerName(String performerName) {
        this.performerName = performerName;
    }

    public String getPerformerAddress() {
        return performerAddress;
    }

    public void setPerformerAddress(String performerAddress) {
        this.performerAddress = performerAddress;
    }

    public String getMedicalDirector() {
        return medicalDirector;
    }

    public void setMedicalDirector(String medicalDirector) {
        this.medicalDirector = medicalDirector;
    }

    public String getCommentSource() {
        return commentSource;
    }

    public void setCommentSource(String commentSource) {
        this.commentSource = commentSource;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public CECodedElementDto getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(CECodedElementDto specimenType) {
        this.specimenType = specimenType;
    }

    public Long getSpecimenDate() {
        return specimenDate;
    }

    public void setSpecimenDate(Long specimenDate) {
        this.specimenDate = specimenDate;
    }

    public Long getSpecimenReceivedDate() {
        return specimenReceivedDate;
    }

    public void setSpecimenReceivedDate(Long specimenReceivedDate) {
        this.specimenReceivedDate = specimenReceivedDate;
    }
}
