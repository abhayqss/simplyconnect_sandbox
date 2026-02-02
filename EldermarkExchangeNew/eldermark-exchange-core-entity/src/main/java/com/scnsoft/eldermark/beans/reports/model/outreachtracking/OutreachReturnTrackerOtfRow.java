package com.scnsoft.eldermark.beans.reports.model.outreachtracking;

import java.util.List;

public class OutreachReturnTrackerOtfRow {
    private List<OutreachReturnTrackerOtfCommunityRow> communityRows;

    public List<OutreachReturnTrackerOtfCommunityRow> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(List<OutreachReturnTrackerOtfCommunityRow> communityRows) {
        this.communityRows = communityRows;
    }
}
