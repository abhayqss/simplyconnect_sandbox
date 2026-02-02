package com.scnsoft.eldermark.shared.carecoordination.notes;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class NoteDto {

    private Long id;
    private Long patientId;
    private NoteEventDto event;
    private String type;
    private NoteSubTypeDto subType;
    private String status;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm a Z")
    private Date lastModifiedDate;
    private String personSubmittingNote;
    private String role;
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private List<RelatedNoteItemDto> historyNotes;
    private NoteResidentAdmittanceHistoryDto noteResidentAdmittanceHistoryDto;

    private Long encouterNoteTypeId;
    private String clinicianCompletingEncounter;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date encounterDate;
    @DateTimeFormat(pattern = "hh:mm a")
    private Date from;
    @DateTimeFormat(pattern = "hh:mm a")
    private Date to;

    private int timeZoneOffset;

    private String encoutnerNoteType;

    private long totalTimeSpent;
    private String range;
    private long units;

    public Long getEncouterNoteTypeId() {
        return encouterNoteTypeId;
    }

    public void setEncouterNoteTypeId(Long encouterType) {
        this.encouterNoteTypeId = encouterType;
    }

    public String getClinicianCompletingEncounter() {
        return clinicianCompletingEncounter;
    }

    public void setClinicianCompletingEncounter(String clinicianCompletingEncounter) {
        this.clinicianCompletingEncounter = clinicianCompletingEncounter;
    }

    public Date getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(Date encounterDate) {
        this.encounterDate = encounterDate;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public NoteEventDto getEvent() {
        return event;
    }

    public void setEvent(NoteEventDto event) {
        this.event = event;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getPersonSubmittingNote() {
        return personSubmittingNote;
    }

    public void setPersonSubmittingNote(String personSubmittingNote) {
        this.personSubmittingNote = personSubmittingNote;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public List<RelatedNoteItemDto> getHistoryNotes() {
        return historyNotes;
    }

    public void setHistoryNotes(List<RelatedNoteItemDto> historyNotes) {
        this.historyNotes = historyNotes;
    }

    public NoteSubTypeDto getSubType() {
        return subType;
    }

    public void setSubType(NoteSubTypeDto subType) {
        this.subType = subType;
    }

    public NoteResidentAdmittanceHistoryDto getNoteResidentAdmittanceHistoryDto() {
        return noteResidentAdmittanceHistoryDto;
    }

    public void setNoteResidentAdmittanceHistoryDto(NoteResidentAdmittanceHistoryDto noteResidentAdmittanceHistoryDto) {
        this.noteResidentAdmittanceHistoryDto = noteResidentAdmittanceHistoryDto;
    }

    public String getEncoutnerNoteType() {
        return encoutnerNoteType;
    }

    public void setEncoutnerNoteType(String encoutnerNoteType) {
        this.encoutnerNoteType = encoutnerNoteType;
    }

    public int getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(int timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public long getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public long getUnits() {
        return units;
    }

    public void setUnits(long units) {
        this.units = units;
    }
}
