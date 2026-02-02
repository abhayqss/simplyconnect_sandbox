package com.scnsoft.eldermark.entity.signature;

public enum DocumentSignatureTemplateStatus {
    DELETED("Deleted"),
    DRAFT("Draft"),
    COMPLETED("Completed");

    private final String displayName;

    DocumentSignatureTemplateStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
