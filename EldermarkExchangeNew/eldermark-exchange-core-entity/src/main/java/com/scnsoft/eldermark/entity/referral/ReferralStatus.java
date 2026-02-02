package com.scnsoft.eldermark.entity.referral;

public enum ReferralStatus {
    PENDING("Pending"),
    PRE_ADMIT("Pre-admit"),
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    CANCELED("Canceled");

    private String displayName;

    ReferralStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
