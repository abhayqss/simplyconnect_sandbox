package com.scnsoft.eldermark.entity.referral;

import java.util.Arrays;

public enum ReferralResponse {
    PRE_ADMIT("Pre-admit", ReferralStatus.PRE_ADMIT),
    ACCEPTED("Accepted", ReferralStatus.ACCEPTED),
    DECLINED("Declined", ReferralStatus.DECLINED);

    private final String value;
    private final ReferralStatus referralStatus;

    ReferralResponse(String value, ReferralStatus referralStatus) {
        this.value = value;
        this.referralStatus = referralStatus;
    }

    public String getValue() {
        return value;
    }

    public ReferralStatus getReferralStatus() {
        return referralStatus;
    }

    public static ReferralResponse fromReferralStatus(ReferralStatus referralStatus) {
        return Arrays.stream(values())
                .filter(response -> response.referralStatus.equals(referralStatus))
                .findFirst()
                .orElseThrow();
    }

}
