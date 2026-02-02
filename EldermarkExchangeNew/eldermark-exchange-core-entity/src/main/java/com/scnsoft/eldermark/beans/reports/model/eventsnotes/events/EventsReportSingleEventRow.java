package com.scnsoft.eldermark.beans.reports.model.eventsnotes.events;

import java.time.Instant;

public class EventsReportSingleEventRow {
    private Instant date;
    private String eventType;
    private String submittedBy;
    private boolean emergencyDepartmentVisit;
    private boolean overNightInPatient;
    private String location;
    private String situation;
    private String background;
    private String assessment;
    private boolean hasInjury;
    private boolean isFollowUpExpected;
    private String followUp;

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public boolean getEmergencyDepartmentVisit() {
        return emergencyDepartmentVisit;
    }

    public void setEmergencyDepartmentVisit(boolean emergencyDepartmentVisit) {
        this.emergencyDepartmentVisit = emergencyDepartmentVisit;
    }

    public boolean getOverNightInPatient() {
        return overNightInPatient;
    }

    public void setOverNightInPatient(boolean overNightInPatient) {
        this.overNightInPatient = overNightInPatient;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public boolean getHasInjury() {
        return hasInjury;
    }

    public void setHasInjury(boolean hasInjury) {
        this.hasInjury = hasInjury;
    }

    public boolean getFollowUpExpected() {
        return isFollowUpExpected;
    }

    public void setFollowUpExpected(boolean followUpExpected) {
        this.isFollowUpExpected = followUpExpected;
    }

    public String getFollowUp() {
        return followUp;
    }

    public void setFollowUp(final String followUp) {
        this.followUp = followUp;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(final String submittedBy) {
        this.submittedBy = submittedBy;
    }
}
