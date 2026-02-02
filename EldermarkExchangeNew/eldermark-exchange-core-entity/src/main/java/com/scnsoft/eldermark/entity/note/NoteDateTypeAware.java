package com.scnsoft.eldermark.entity.note;

import java.time.Instant;

public interface NoteDateTypeAware extends NoteTypeAware {
    Instant getNoteDate();
}
