package com.scnsoft.eldermark.beans.reports.model.outreachtracking;

import java.util.List;

public class OutReachReturnTrackerRtfRow {
    private List<OutreachReturnTrackerRtfCommunityRow> communityRows;

    public List<OutreachReturnTrackerRtfCommunityRow> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(List<OutreachReturnTrackerRtfCommunityRow> communityRows) {
        this.communityRows = communityRows;
    }
}
