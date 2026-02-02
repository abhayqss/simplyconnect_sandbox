package com.scnsoft.eldermark.beans.reports.model.cstracking;

import java.util.List;

public class CsTrackingReportCommunityRow {
    private String communityName;
    private List<CsTrackingReportClientRow> clientRows;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public List<CsTrackingReportClientRow> getClientRows() {
        return clientRows;
    }

    public void setClientRows(List<CsTrackingReportClientRow> clientRows) {
        this.clientRows = clientRows;
    }
}