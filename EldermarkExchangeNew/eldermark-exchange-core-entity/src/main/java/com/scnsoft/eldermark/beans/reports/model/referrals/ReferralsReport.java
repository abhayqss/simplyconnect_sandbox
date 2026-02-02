package com.scnsoft.eldermark.beans.reports.model.referrals;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class ReferralsReport extends Report {

    private List<TotalReferralsRow> totalReferralsRows;

    private List<ReferReferralsRow> inReferReferralsRows;

    private List<InboundIndividualReferReferralRow> inIndividualReferReferralRows;

    private List<ReferReferralsRow> outReferReferralsRows;

    private List<OutboundIndividualReferReferralRow> outIndividualReferReferralRows;

    public List<TotalReferralsRow> getTotalReferralsRows() {
        return totalReferralsRows;
    }

    public void setTotalReferralsRows(List<TotalReferralsRow> totalReferralsRows) {
        this.totalReferralsRows = totalReferralsRows;
    }

    public List<ReferReferralsRow> getInReferReferralsRows() {
        return inReferReferralsRows;
    }

    public void setInReferReferralsRows(List<ReferReferralsRow> inReferReferralsRows) {
        this.inReferReferralsRows = inReferReferralsRows;
    }

    public List<InboundIndividualReferReferralRow> getInIndividualReferReferralRows() {
        return inIndividualReferReferralRows;
    }

    public void setInIndividualReferReferralRows(List<InboundIndividualReferReferralRow> inIndividualReferReferralRows) {
        this.inIndividualReferReferralRows = inIndividualReferReferralRows;
    }

    public List<ReferReferralsRow> getOutReferReferralsRows() {
        return outReferReferralsRows;
    }

    public void setOutReferReferralsRows(List<ReferReferralsRow> outReferReferralsRows) {
        this.outReferReferralsRows = outReferReferralsRows;
    }

    public List<OutboundIndividualReferReferralRow> getOutIndividualReferReferralRows() {
        return outIndividualReferReferralRows;
    }

    public void setOutIndividualReferReferralRows(List<OutboundIndividualReferReferralRow> outIndividualReferReferralRows) {
        this.outIndividualReferReferralRows = outIndividualReferReferralRows;
    }
}
