package com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes;

import java.time.Instant;

public class NotesReportSingleNoteRow {
    private Instant date;
    private String submittedBy;
    private String encounterType;
    private Long units;
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private String noteType;
    private Instant encounterDate;
    private String personCompletingEncounter;

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public Long getUnits() {
        return units;
    }

    public void setUnits(Long units) {
        this.units = units;
    }

    public String getSubjective() {
        return subjective;
    }

    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(final String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Instant getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(final Instant encounterDate) {
        this.encounterDate = encounterDate;
    }

    public String getPersonCompletingEncounter() {
        return personCompletingEncounter;
    }

    public void setPersonCompletingEncounter(final String personCompletingEncounter) {
        this.personCompletingEncounter = personCompletingEncounter;
    }
}
