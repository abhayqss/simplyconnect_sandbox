package com.scnsoft.eldermark.beans.reports.model.staffcaseload;

import org.springframework.lang.Nullable;

public class ResidentStaffCaseLoadItem {

    private Long clientId;
    private String clientName;
    private String community;
    @Nullable
    private Float averageScore;

    public ResidentStaffCaseLoadItem(Long clientId, String clientName, String community, @Nullable Float averageScore) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.community = community;
        this.averageScore = averageScore;
    }

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

    @Nullable
    public Float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(@Nullable Float averageScore) {
        this.averageScore = averageScore;
    }
}
