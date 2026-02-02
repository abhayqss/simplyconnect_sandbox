package com.scnsoft.eldermark.dto.serviceplan;

import java.util.List;

public class ClientDashboardServicePlanNeedDto extends BaseServicePlanNeedDto {

    private String title;

    private String domainTitle;

    private List<ClientDashboardServicePlanGoalDto> goals;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDomainTitle() {
        return domainTitle;
    }

    public void setDomainTitle(String domainTitle) {
        this.domainTitle = domainTitle;
    }

    public List<ClientDashboardServicePlanGoalDto> getGoals() {
        return goals;
    }

    public void setGoals(List<ClientDashboardServicePlanGoalDto> goals) {
        this.goals = goals;
    }
}
