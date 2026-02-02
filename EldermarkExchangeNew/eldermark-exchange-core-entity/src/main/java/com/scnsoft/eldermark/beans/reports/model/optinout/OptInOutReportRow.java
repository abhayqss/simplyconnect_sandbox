package com.scnsoft.eldermark.beans.reports.model.optinout;

import java.util.List;

public class OptInOutReportRow {
    private List<OptInOutReportCommunityRow> communityRows;

    public List<OptInOutReportCommunityRow> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(List<OptInOutReportCommunityRow> communityRows) {
        this.communityRows = communityRows;
    }
}
