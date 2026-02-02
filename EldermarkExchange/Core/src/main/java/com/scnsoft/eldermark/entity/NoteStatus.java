package com.scnsoft.eldermark.entity;

public enum NoteStatus {
    CREATED("Created"),
    UPDATED("Updated");

    private String displayName;

    NoteStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
