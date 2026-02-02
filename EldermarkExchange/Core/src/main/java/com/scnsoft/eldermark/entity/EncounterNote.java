package com.scnsoft.eldermark.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "EncounterNote")
public class EncounterNote extends Note{
    
    @ManyToOne
    @JoinColumn(name = "encounter_note_type_id", referencedColumnName = "id")
    private EncounterNoteType encounterNoteType;
    
    @Column(name = "other_clinician_completing_encounter", nullable = true)
    private String clinicianCompletingEncounter;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "encounter_date", nullable = false)
    private Date encounterDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_from", nullable = false)
    private Date fromTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_to", nullable = false)
    private Date toTime;

    public EncounterNoteType getEncounterNoteType() {
        return encounterNoteType;
    }

    public void setEncounterNoteType(EncounterNoteType encounterNoteType) {
        this.encounterNoteType = encounterNoteType;
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

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

}