package com.scnsoft.eldermark.entity.note;

public enum NoteType {

    EVENT_NOTE("Event Note"),
    PATIENT_NOTE("Client Note"),
    GROUP_NOTE("Group Note");

    private String displayName;

    NoteType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
