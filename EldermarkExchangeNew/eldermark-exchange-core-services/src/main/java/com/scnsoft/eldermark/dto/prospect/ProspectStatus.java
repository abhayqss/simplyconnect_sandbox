package com.scnsoft.eldermark.dto.prospect;

public enum ProspectStatus {
    ALL("All"),
    ACTIVE("Active"),
    CONVERTED_TO_CLIENT("Converted to Client"),
    INACTIVE("Inactive");

    private final String displayName;

    ProspectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
