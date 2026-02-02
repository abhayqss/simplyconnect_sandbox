package com.scnsoft.eldermark.mobile.dto.conversation.call.history;

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
