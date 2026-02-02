package com.scnsoft.eldermark.beans.reports.model.outreachtracking;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class OutreachReturnTrackerReport extends Report {
    private List<OutreachReturnTrackerOtfRow> outreachReturnTrackerOtfRows;
    private List<OutReachReturnTrackerRtfRow> outReachReturnTrackerRtfRows;

    public List<OutreachReturnTrackerOtfRow> getOutreachReturnTrackerOtfRows() {
        return outreachReturnTrackerOtfRows;
    }

    public void setOutreachReturnTrackerOtfRows(List<OutreachReturnTrackerOtfRow> outreachReturnTrackerOtfRows) {
        this.outreachReturnTrackerOtfRows = outreachReturnTrackerOtfRows;
    }

    public List<OutReachReturnTrackerRtfRow> getOutReachReturnTrackerRtfRows() {
        return outReachReturnTrackerRtfRows;
    }

    public void setOutReachReturnTrackerRtfRows(List<OutReachReturnTrackerRtfRow> outReachReturnTrackerRtfRows) {
        this.outReachReturnTrackerRtfRows = outReachReturnTrackerRtfRows;
    }
}
