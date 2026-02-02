package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.note.EncounterNoteType;

import java.time.Instant;

public interface EncounterNoteDetailsAware extends ClientIdAware {

    Long getId();

    Instant getEncounterFromTime();

    Instant getEncounterToTime();

    EncounterNoteType getEncounterNoteType();

    Instant getEncounterDate();

    String getClinicianCompletingEncounterFirstName();

    String getClinicianCompletingEncounterLastName();
}
