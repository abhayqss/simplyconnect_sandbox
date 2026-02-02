package com.scnsoft.eldermark.shared.ccd;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ProblemObservationDto extends BaseCcdEntryDto {

    private Long id;

    private CcdCodeDto value;

    private Boolean primary;

    private CcdCodeDto type;

    private CcdCodeDto status;

    @DateTimeFormat(pattern = "MM/DD/YYYY hh:mm A (Z)")
    private Date startDate;

    @DateTimeFormat(pattern = "MM/DD/YYYY hh:mm A (Z)")
    private Date endDate;

    private Date onSetDate;

    private Date recordedDate;

    private String recordedBy;

    private String comments;

    private String healthStatusObservation;

    private String dataSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CcdCodeDto getValue() {
        return value;
    }

    public void setValue(CcdCodeDto value) {
        this.value = value;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public CcdCodeDto getType() {
        return type;
    }

    public void setType(CcdCodeDto type) {
        this.type = type;
    }

    public CcdCodeDto getStatus() {
        return status;
    }

    public void setStatus(CcdCodeDto status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getOnSetDate() {
        return onSetDate;
    }

    public void setOnSetDate(Date onSetDate) {
        this.onSetDate = onSetDate;
    }

    public Date getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Date recordedDate) {
        this.recordedDate = recordedDate;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String noteText) {
        this.comments = noteText;
    }

    public String getHealthStatusObservation() {
        return healthStatusObservation;
    }

    public void setHealthStatusObservation(String healthStatusObservation) {
        this.healthStatusObservation = healthStatusObservation;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
