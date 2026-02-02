package com.scnsoft.eldermark.entity.lab;

public enum LabResearchOrderReason {
    NEW_ADMIT("New admit"),
    NEW_TEAM_MEMBER("New team member"),
    RESIDENT("Resident"),
    TEAM_MEMBER("Team member");

    private final String value;

    LabResearchOrderReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
