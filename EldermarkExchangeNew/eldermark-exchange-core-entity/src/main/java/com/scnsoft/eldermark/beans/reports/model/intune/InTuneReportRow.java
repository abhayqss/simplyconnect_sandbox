package com.scnsoft.eldermark.beans.reports.model.intune;

import java.util.List;

public class InTuneReportRow {

    private String communityName;
    private List<InTuneReportRowClient> clientRows;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public List<InTuneReportRowClient> getClientRows() {
        return clientRows;
    }

    public void setClientRows(List<InTuneReportRowClient> clientRows) {
        this.clientRows = clientRows;
    }
}

