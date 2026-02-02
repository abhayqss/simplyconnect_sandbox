package com.scnsoft.eldermark.entity.hieconsentpolicy;

public enum HieConsentPolicyObtainedBy {
    CLIENT("Client"),
    RESPONSIBLE_PARTY("Responsible party"),
    REPRESENTATIVE("Representative");
    private final String displayName;

    HieConsentPolicyObtainedBy(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
