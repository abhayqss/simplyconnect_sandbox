package com.scnsoft.eldermark.beans.reports.model.referrals;

public class TotalReferralsRow extends ReferralRow {

    private long totalNumberOfInboundReferrals;

    private long totalNumberOfOutboundReferrals;

    public long getTotalNumberOfInboundReferrals() {
        return totalNumberOfInboundReferrals;
    }

    public void setTotalNumberOfInboundReferrals(long totalNumberOfInboundReferrals) {
        this.totalNumberOfInboundReferrals = totalNumberOfInboundReferrals;
    }

    public long getTotalNumberOfOutboundReferrals() {
        return totalNumberOfOutboundReferrals;
    }

    public void setTotalNumberOfOutboundReferrals(long totalNumberOfOutboundReferrals) {
        this.totalNumberOfOutboundReferrals = totalNumberOfOutboundReferrals;
    }
}
