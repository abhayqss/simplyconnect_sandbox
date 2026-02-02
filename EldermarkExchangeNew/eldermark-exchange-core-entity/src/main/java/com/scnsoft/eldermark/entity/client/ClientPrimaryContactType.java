package com.scnsoft.eldermark.entity.client;

public enum ClientPrimaryContactType {

    SELF("Self"),
    CARE_TEAM_MEMBER("Care team member");

    private String displayName;

    ClientPrimaryContactType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
