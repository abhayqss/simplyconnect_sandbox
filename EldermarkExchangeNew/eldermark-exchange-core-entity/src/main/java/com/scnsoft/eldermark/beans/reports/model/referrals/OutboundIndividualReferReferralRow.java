package com.scnsoft.eldermark.beans.reports.model.referrals;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OutboundIndividualReferReferralRow extends ReferReferralRow {

    private String ReferringIndividualName;

    private long numberOfAcceptedReferrals;

    private double referralSuccessRate;

    public String getReferringIndividualName() {
        return ReferringIndividualName;
    }

    public void setReferringIndividualName(String referringIndividualName) {
        ReferringIndividualName = referringIndividualName;
    }

    public long getNumberOfAcceptedReferrals() {
        return numberOfAcceptedReferrals;
    }

    public void setNumberOfAcceptedReferrals(long numberOfAcceptedReferrals) {
        this.numberOfAcceptedReferrals = numberOfAcceptedReferrals;
    }

    public double getReferralSuccessRate() {
        return referralSuccessRate;
    }

    public void setReferralSuccessRate(double referralSuccessRate) {
        this.referralSuccessRate = new BigDecimal(referralSuccessRate).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
