package com.scnsoft.eldermark.beans.reports.model.cstracking;

import java.util.List;

public class CsTrackingReportRow {
    private List<CsTrackingReportCommunityRow> communityRows;

    public List<CsTrackingReportCommunityRow> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(List<CsTrackingReportCommunityRow> communityRows) {
        this.communityRows = communityRows;
    }
}