package com.scnsoft.eldermark.dto.conversation;

public enum CallHistoryType {
    INCOMING("Incoming"),
    OUTGOING("Outgoing"),
    MISSED("Missed");

    private final String displayName;

    CallHistoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
