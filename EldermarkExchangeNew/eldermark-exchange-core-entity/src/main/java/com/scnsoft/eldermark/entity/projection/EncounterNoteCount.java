package com.scnsoft.eldermark.entity.projection;

import com.scnsoft.eldermark.entity.note.NoteSubType.EncounterCode;

public class EncounterNoteCount {

    private EncounterCode code;

    private Long count;

    public EncounterNoteCount(EncounterCode code, Long count) {
        this.code = code;
        this.count = count;
    }

    public EncounterCode getCode() {
        return code;
    }

    public void setCode(EncounterCode code) {
        this.code = code;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
