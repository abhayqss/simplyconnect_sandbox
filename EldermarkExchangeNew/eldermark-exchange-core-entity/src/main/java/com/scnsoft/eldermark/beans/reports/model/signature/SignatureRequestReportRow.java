package com.scnsoft.eldermark.beans.reports.model.signature;

import java.util.List;

public class SignatureRequestReportRow{
    private String organizationName;
    private List<SignatureRequestReportRowCommunity> communityRows;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
    }

    public List<SignatureRequestReportRowCommunity> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(final List<SignatureRequestReportRowCommunity> communityRows) {
        this.communityRows = communityRows;
    }
}
