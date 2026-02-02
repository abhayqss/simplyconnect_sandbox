package com.scnsoft.eldermark.dump.model;

public class SPIndividual {

    private String patient;
    private String community;
    private String resourceName;
    private Integer scoreOfTiedToServicePlanNeed;
    private String domain;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Integer getScoreOfTiedToServicePlanNeed() {
        return scoreOfTiedToServicePlanNeed;
    }

    public void setScoreOfTiedToServicePlanNeed(Integer scoreOfTiedToServicePlanNeed) {
        this.scoreOfTiedToServicePlanNeed = scoreOfTiedToServicePlanNeed;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }
}
