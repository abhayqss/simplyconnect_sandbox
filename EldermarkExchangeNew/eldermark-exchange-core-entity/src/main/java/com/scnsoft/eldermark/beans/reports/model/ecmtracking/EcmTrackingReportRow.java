package com.scnsoft.eldermark.beans.reports.model.ecmtracking;

import java.util.List;

public class EcmTrackingReportRow {
    private List<EcmTrackingReportCommunityRow> communityRows;

    public List<EcmTrackingReportCommunityRow> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(List<EcmTrackingReportCommunityRow> communityRows) {
        this.communityRows = communityRows;
    }
}