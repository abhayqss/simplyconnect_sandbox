package com.scnsoft.eldermark.beans.reports.model;

public class TotalClientsTab {

    private String communityName;

    private String clientName;

    private Long clientId;

    private Long inPersonTimeWithIndividualsTotalMin;

    private Long phoneCallTimeWithIndividualsOrServicesTotalMin;

    private Long totalMinutes;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getInPersonTimeWithIndividualsTotalMin() {
        return inPersonTimeWithIndividualsTotalMin;
    }

    public void setInPersonTimeWithIndividualsTotalMin(Long inPersonTimeWithIndividualsTotalMin) {
        this.inPersonTimeWithIndividualsTotalMin = inPersonTimeWithIndividualsTotalMin;
    }

    public Long getPhoneCallTimeWithIndividualsOrServicesTotalMin() {
        return phoneCallTimeWithIndividualsOrServicesTotalMin;
    }

    public void setPhoneCallTimeWithIndividualsOrServicesTotalMin(Long phoneCallTimeWithIndividualsOrServicesTotalMin) {
        this.phoneCallTimeWithIndividualsOrServicesTotalMin = phoneCallTimeWithIndividualsOrServicesTotalMin;
    }

    public Long getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Long totalMinutes) {
        this.totalMinutes = totalMinutes;
    }
}
