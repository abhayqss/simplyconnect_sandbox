package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class SPIndividualDomain {

    private String domainName;

    private Integer scoreOfTiedToServicePlanNeed;

    private List<String> resourceNames;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Integer getScoreOfTiedToServicePlanNeed() {
        return scoreOfTiedToServicePlanNeed;
    }

    public void setScoreOfTiedToServicePlanNeed(Integer scoreOfTiedToServicePlanNeed) {
        this.scoreOfTiedToServicePlanNeed = scoreOfTiedToServicePlanNeed;
    }

    public List<String> getResourceNames() {
        return resourceNames;
    }

    public void setResourceNames(List<String> resourceNames) {
        this.resourceNames = resourceNames;
    }
}
