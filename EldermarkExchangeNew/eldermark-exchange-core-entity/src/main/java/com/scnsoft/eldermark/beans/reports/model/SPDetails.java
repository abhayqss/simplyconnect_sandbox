package com.scnsoft.eldermark.beans.reports.model;

public class SPDetails {

    private String patientName;
    private Long patientId;
    private String community;
    private String status;
    private Long daysToComplete;
    private long eventsCount;
    private long goalsCount;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDaysToComplete() {
        return daysToComplete;
    }

    public void setDaysToComplete(Long daysToComplete) {
        this.daysToComplete = daysToComplete;
    }

    public long getEventsCount() {
        return eventsCount;
    }

    public void setEventsCount(long eventsCount) {
        this.eventsCount = eventsCount;
    }

    public long getGoalsCount() {
        return goalsCount;
    }

    public void setGoalsCount(long goalsCount) {
        this.goalsCount = goalsCount;
    }
}
