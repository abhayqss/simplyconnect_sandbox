package com.scnsoft.eldermark.dto.notes;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

public class NoteTypeDto extends IdentifiedNamedTitledEntityDto {

    private String followUpCode;

    private String encounterCode;

    private boolean canCreate;

    private boolean canCreateGroupNote;

    private boolean canCreateEventNote;

    public boolean isCanCreate() {
        return canCreate;
    }

    public void setCanCreate(boolean canCreate) {
        this.canCreate = canCreate;
    }

    public String getFollowUpCode() {
        return followUpCode;
    }

    public void setFollowUpCode(String followUpCode) {
        this.followUpCode = followUpCode;
    }

    public String getEncounterCode() {
        return encounterCode;
    }

    public void setEncounterCode(String encounterCode) {
        this.encounterCode = encounterCode;
    }

    public boolean getCanCreateGroupNote() {
        return canCreateGroupNote;
    }

    public void setCanCreateGroupNote(boolean canCreateGroupNote) {
        this.canCreateGroupNote = canCreateGroupNote;
    }

    public boolean getCanCreateEventNote() {
        return canCreateEventNote;
    }

    public void setCanCreateEventNote(boolean canCreateEventNote) {
        this.canCreateEventNote = canCreateEventNote;
    }
}
