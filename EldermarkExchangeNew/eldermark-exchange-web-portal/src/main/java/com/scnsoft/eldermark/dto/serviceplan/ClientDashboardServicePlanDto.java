package com.scnsoft.eldermark.dto.serviceplan;

import java.util.List;

public class ClientDashboardServicePlanDto extends BaseServicePlanDto {

    private String statusName;

    private String statusTitle;

    private List<ClientDashboardServicePlanNeedDto> needs;


    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public List<ClientDashboardServicePlanNeedDto> getNeeds() {
        return needs;
    }

    public void setNeeds(List<ClientDashboardServicePlanNeedDto> needs) {
        this.needs = needs;
    }
}