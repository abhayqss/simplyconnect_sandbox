package com.scnsoft.eldermark.entity.community;

public enum DeviceTypeWorkflow {
    ACTION("Action"),
    NO_ACTION("No Action");

    private String displayName;

    DeviceTypeWorkflow(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
