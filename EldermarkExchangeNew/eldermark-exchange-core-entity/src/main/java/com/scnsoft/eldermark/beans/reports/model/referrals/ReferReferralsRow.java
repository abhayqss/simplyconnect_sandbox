package com.scnsoft.eldermark.beans.reports.model.referrals;

public class ReferReferralsRow extends ReferReferralRow {

    private long totalNumberOfReferrals;

    private long numberOfPendingReferrals;

    private long numberOfPreadmitReferrals;

    private long numberOfAcceptedReferrals;

    private long numberOfDeclinedReferrals;

    private long numberOfCanceledReferrals;

    public long getTotalNumberOfReferrals() {
        return totalNumberOfReferrals;
    }

    public void setTotalNumberOfReferrals(long totalNumberOfReferrals) {
        this.totalNumberOfReferrals = totalNumberOfReferrals;
    }

    public long getNumberOfPendingReferrals() {
        return numberOfPendingReferrals;
    }

    public void setNumberOfPendingReferrals(long numberOfPendingReferrals) {
        this.numberOfPendingReferrals = numberOfPendingReferrals;
    }

    public long getNumberOfPreadmitReferrals() {
        return numberOfPreadmitReferrals;
    }

    public void setNumberOfPreadmitReferrals(long numberOfPreadmitReferrals) {
        this.numberOfPreadmitReferrals = numberOfPreadmitReferrals;
    }

    public long getNumberOfAcceptedReferrals() {
        return numberOfAcceptedReferrals;
    }

    public void setNumberOfAcceptedReferrals(long numberOfAcceptedReferrals) {
        this.numberOfAcceptedReferrals = numberOfAcceptedReferrals;
    }

    public long getNumberOfDeclinedReferrals() {
        return numberOfDeclinedReferrals;
    }

    public void setNumberOfDeclinedReferrals(long numberOfDeclinedReferrals) {
        this.numberOfDeclinedReferrals = numberOfDeclinedReferrals;
    }

    public long getNumberOfCanceledReferrals() {
        return numberOfCanceledReferrals;
    }

    public void setNumberOfCanceledReferrals(long numberOfCanceledReferrals) {
        this.numberOfCanceledReferrals = numberOfCanceledReferrals;
    }
}
