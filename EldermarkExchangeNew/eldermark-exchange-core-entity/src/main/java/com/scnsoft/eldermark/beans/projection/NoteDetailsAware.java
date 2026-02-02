package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.note.NoteSubType;
import com.scnsoft.eldermark.entity.note.NoteType;

import java.time.Instant;
import java.util.Set;

public interface NoteDetailsAware extends ClientIdNamesAware, ClientCommunityIdNameAware, ClientOrganizationIdNameAware {

    Long getId();

    Instant getNoteDate();

    Instant getLastModifiedDate();

    NoteType getType();

    NoteSubType getSubType();

    String getSubjective();

    String getObjective();

    String getAssessment();

    String getPlan();

    Set<Long> getNoteClientIds();

    String getEmployeeFirstName();

    String getEmployeeLastName();

    Instant getEncounterFromTime();

    Instant getEncounterToTime();

    Instant getEncounterDate();

    String getClinicianCompletingEncounterFirstName();

    String getClinicianCompletingEncounterLastName();


}
