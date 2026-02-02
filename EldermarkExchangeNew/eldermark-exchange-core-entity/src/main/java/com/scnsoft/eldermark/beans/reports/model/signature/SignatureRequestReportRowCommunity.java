package com.scnsoft.eldermark.beans.reports.model.signature;

import java.util.List;

public class SignatureRequestReportRowCommunity {
    private String communityName;
    private List<SignatureRequestReportRowClient> clientRows;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(final String communityName) {
        this.communityName = communityName;
    }

    public List<SignatureRequestReportRowClient> getClientRows() {
        return clientRows;
    }

    public void setClientRows(final List<SignatureRequestReportRowClient> clientRows) {
        this.clientRows = clientRows;
    }
}
