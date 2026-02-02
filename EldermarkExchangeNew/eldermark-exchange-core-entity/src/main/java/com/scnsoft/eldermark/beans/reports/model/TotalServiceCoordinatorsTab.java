package com.scnsoft.eldermark.beans.reports.model;

public class TotalServiceCoordinatorsTab {

    private String communityName;

    private String serviceCoordinatorName;

    private Long inPersonTimeWithIndividualsTotalMin;

    private Long phoneCallTimeWithIndividualsTotalMin;

    private Long totalMin;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getServiceCoordinatorName() {
        return serviceCoordinatorName;
    }

    public void setServiceCoordinatorName(String serviceCoordinatorName) {
        this.serviceCoordinatorName = serviceCoordinatorName;
    }

    public Long getInPersonTimeWithIndividualsTotalMin() {
        return inPersonTimeWithIndividualsTotalMin;
    }

    public void setInPersonTimeWithIndividualsTotalMin(Long inPersonTimeWithIndividualsTotalMin) {
        this.inPersonTimeWithIndividualsTotalMin = inPersonTimeWithIndividualsTotalMin;
    }

    public Long getPhoneCallTimeWithIndividualsTotalMin() {
        return phoneCallTimeWithIndividualsTotalMin;
    }

    public void setPhoneCallTimeWithIndividualsTotalMin(Long phoneCallTimeWithIndividualsTotalMin) {
        this.phoneCallTimeWithIndividualsTotalMin = phoneCallTimeWithIndividualsTotalMin;
    }

    public Long getTotalMin() {
        return totalMin;
    }

    public void setTotalMin(Long totalMin) {
        this.totalMin = totalMin;
    }
}
