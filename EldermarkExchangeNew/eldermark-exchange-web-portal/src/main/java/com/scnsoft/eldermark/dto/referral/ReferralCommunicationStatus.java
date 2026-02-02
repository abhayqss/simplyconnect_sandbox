package com.scnsoft.eldermark.dto.referral;

public enum ReferralCommunicationStatus {
    PENDING("Pending"),
    REPLIED("Replied");

    private final String displayName;

    ReferralCommunicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
