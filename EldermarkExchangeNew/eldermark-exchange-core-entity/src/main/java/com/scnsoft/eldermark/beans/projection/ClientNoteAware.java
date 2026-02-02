package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.note.NoteType;

import java.time.Instant;
import java.util.Set;

public interface ClientNoteAware extends ClientIdNamesAware, IdAware {
    Instant getNoteDate();
    Instant getLastModifiedDate();
    NoteType getType();
}