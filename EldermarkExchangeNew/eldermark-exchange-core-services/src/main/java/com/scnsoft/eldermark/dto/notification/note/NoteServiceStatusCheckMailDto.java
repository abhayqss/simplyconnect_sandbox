package com.scnsoft.eldermark.dto.notification.note;

public class NoteServiceStatusCheckMailDto implements NoteServiceStatusCheckViewData {

    private Long servicePlanId;
    private Long servicePlanCreatedDate;
    private String resourceName;
    private String providerName;
    private String auditPerson;
    private Long checkDate;
    private Long nextCheckDate;
    private Boolean serviceProvided;
    private boolean canViewServicePlan;

    @Override
    public Long getServicePlanId() {
        return servicePlanId;
    }

    @Override
    public void setServicePlanId(Long servicePlanId) {
        this.servicePlanId = servicePlanId;
    }

    @Override
    public Long getServicePlanCreatedDate() {
        return servicePlanCreatedDate;
    }

    @Override
    public void setServicePlanCreatedDate(Long servicePlanCreatedDate) {
        this.servicePlanCreatedDate = servicePlanCreatedDate;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public String getAuditPerson() {
        return auditPerson;
    }

    @Override
    public void setAuditPerson(String auditPerson) {
        this.auditPerson = auditPerson;
    }

    @Override
    public Long getCheckDate() {
        return checkDate;
    }

    @Override
    public void setCheckDate(Long checkDate) {
        this.checkDate = checkDate;
    }

    @Override
    public Long getNextCheckDate() {
        return nextCheckDate;
    }

    @Override
    public void setNextCheckDate(Long nextCheckDate) {
        this.nextCheckDate = nextCheckDate;
    }

    @Override
    public Boolean getServiceProvided() {
        return serviceProvided;
    }

    @Override
    public void setServiceProvided(Boolean serviceProvided) {
        this.serviceProvided = serviceProvided;
    }

    @Override
    public boolean getCanViewServicePlan() {
        return canViewServicePlan;
    }

    @Override
    public void setCanViewServicePlan(boolean canViewServicePlan) {
        this.canViewServicePlan = canViewServicePlan;
    }
}
