package com.scnsoft.eldermark.entity.note;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class EncounterNote extends Note {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "encounter_note_type_id", referencedColumnName = "id")
    private EncounterNoteType encounterNoteType;
    
    @Column(name = "facility_code", nullable = false)
    private String facilityCode;

    public EncounterNoteType getEncounterNoteType() {
        return encounterNoteType;
    }

    public void setEncounterNoteType(EncounterNoteType encounterNoteType) {
        this.encounterNoteType = encounterNoteType;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }
}
