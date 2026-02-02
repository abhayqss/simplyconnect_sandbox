package com.scnsoft.eldermark.shared.carecoordination.notes;

import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

public class NoteSubTypeDto extends KeyValueDto {

    private NoteSubType.FollowUpCode followUpCode;
    private NoteSubType.EncounterCode encounterCode;
    private boolean phrHidden;

    public NoteSubTypeDto() {
    }

    public NoteSubTypeDto(Long id, String label) {
        super(id, label);
    }

    public NoteSubTypeDto(Long id, String label, NoteSubType.FollowUpCode followUpCode) {
        super(id, label);
        this.followUpCode = followUpCode;
    }

    public NoteSubTypeDto(Long id, String label, NoteSubType.FollowUpCode followUpCode,
            NoteSubType.EncounterCode encounterCode, boolean phrHidden) {
        super(id, label);
        this.followUpCode = followUpCode;
        this.encounterCode = encounterCode;
        this.phrHidden = phrHidden;
    }

    public NoteSubType.FollowUpCode getFollowUpCode() {
        return followUpCode;
    }

    public void setFollowUpCode(NoteSubType.FollowUpCode followUpCode) {
        this.followUpCode = followUpCode;
    }

    public NoteSubType.EncounterCode getEncounterCode() {
        return encounterCode;
    }

    public void setEncounterCode(NoteSubType.EncounterCode encounterCode) {
        this.encounterCode = encounterCode;
    }

    public boolean isPhrHidden() {
        return phrHidden;
    }

    public void setPhrHidden(boolean phrHidden) {
        this.phrHidden = phrHidden;
    }
}
