package com.scnsoft.eldermark.shared.carecoordination.notes;

import java.util.Date;

public class RelatedNoteItemDto {
    private Long id;
    private Long patientId;
    private String status;
    private Date lastModifiedDate;
    private String personSubmittingNote;
    private String role;

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
}
