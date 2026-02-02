package com.scnsoft.eldermark.beans.reports.model.ecmtracking;

import java.util.List;

public class EcmTrackingReportCommunityRow {
    private String communityName;
    private List<EcmTrackingReportClientRow> clientRows;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public List<EcmTrackingReportClientRow> getClientRows() {
        return clientRows;
    }

    public void setClientRows(List<EcmTrackingReportClientRow> clientRows) {
        this.clientRows = clientRows;
    }
}