package com.scnsoft.eldermark.entity.hieconsentpolicy;

public enum HieConsentPolicySource {
    WEB("Web app"),
    MOBILE("Mobile app");
    private final String displayName;

    HieConsentPolicySource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
