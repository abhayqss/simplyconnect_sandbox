package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.note.NoteSubType;
import com.scnsoft.eldermark.entity.note.NoteType;

public interface NoteOutreachReportDetailsAware extends ClientIdAware, IdAware {
    NoteType getType();
    NoteSubType getSubType();
}
