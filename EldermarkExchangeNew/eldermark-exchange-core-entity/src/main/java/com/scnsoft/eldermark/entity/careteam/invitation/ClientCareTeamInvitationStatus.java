package com.scnsoft.eldermark.entity.careteam.invitation;

public enum ClientCareTeamInvitationStatus {
    PENDING("Pending", true, true),
    EXPIRED("Expired", true, false),
    DECLINED("Declined", true, true),
    CANCELED("Canceled", false, false),
    ACCEPTED("Accepted", false, false);

    private final String displayName;
    private final boolean isResendable;
    private final boolean isCancelable;

    ClientCareTeamInvitationStatus(String displayName, boolean isResendable, boolean isCancelable) {
        this.displayName = displayName;
        this.isResendable = isResendable;
        this.isCancelable = isCancelable;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isResendable() {
        return isResendable;
    }

    public boolean isCancelable() {
        return isCancelable;
    }
}
