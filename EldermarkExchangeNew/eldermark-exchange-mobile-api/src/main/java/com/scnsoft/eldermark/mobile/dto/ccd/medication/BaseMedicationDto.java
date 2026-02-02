package com.scnsoft.eldermark.mobile.dto.ccd.medication;

public class BaseMedicationDto {

    private Long id;
    private String name;
    private Long startedDate;
    private Long stoppedDate;
    private Boolean prnScheduled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Long startedDate) {
        this.startedDate = startedDate;
    }

    public Long getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Long stoppedDate) {
        this.stoppedDate = stoppedDate;
    }

    public Boolean getPrnScheduled() {
        return prnScheduled;
    }

    public void setPrnScheduled(Boolean prnScheduled) {
        this.prnScheduled = prnScheduled;
    }
}
