package com.scnsoft.eldermark.beans.reports.model.staffcaseload;

public class ResidentStaffCareTeamItem {

    private Long clientId;
    private String clientName;
    private String community;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }
}
