package com.scnsoft.eldermark.entity.note;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

import java.util.Set;

public interface NoteClientIdClientIdsTypeAware extends ClientIdAware, NoteTypeAware {
    Set<Long> getNoteClientIds();
}
