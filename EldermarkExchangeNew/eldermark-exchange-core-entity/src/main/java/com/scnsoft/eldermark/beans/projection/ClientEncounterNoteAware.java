package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.note.EncounterNoteType;

import java.time.Instant;

public interface ClientEncounterNoteAware extends ClientIdAware, IdAware {
    Instant getEncounterDate();
    EncounterNoteType getEncounterNoteType();
}