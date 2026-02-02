package com.scnsoft.eldermark.entity;

public enum NoteType {

    PATIENT_NOTE("Patient Note"),
    EVENT_NOTE("Event Note"),
    GROUP_NOTE("Group Note");

    private String displayName;

    NoteType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
