package com.scnsoft.eldermark.beans.reports.model.referrals;

public abstract class ReferReferralRow extends ReferralRow {

    private String ReferCommunityName;

    public String getReferCommunityName() {
        return ReferCommunityName;
    }

    public void setReferCommunityName(String referCommunityName) {
        ReferCommunityName = referCommunityName;
    }

}
