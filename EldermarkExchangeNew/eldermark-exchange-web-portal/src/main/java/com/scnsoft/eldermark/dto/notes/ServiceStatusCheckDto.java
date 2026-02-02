package com.scnsoft.eldermark.dto.notes;

import com.scnsoft.eldermark.dto.notification.note.NoteServiceStatusCheckViewData;

import javax.validation.constraints.NotNull;

public class ServiceStatusCheckDto implements NoteServiceStatusCheckViewData {
    @NotNull
    private Long servicePlanId;

    private Long servicePlanCreatedDate;

    @NotNull
    private String resourceName;

    private String providerName;

    @NotNull
    private String auditPerson;

    @NotNull
    private Long checkDate;

    private Long nextCheckDate;

    @NotNull
    private Boolean serviceProvided;

    private boolean canViewServicePlan;

    public Long getServicePlanId() {
        return servicePlanId;
    }

    public void setServicePlanId(Long servicePlanId) {
        this.servicePlanId = servicePlanId;
    }

    public Long getServicePlanCreatedDate() {
        return servicePlanCreatedDate;
    }

    public void setServicePlanCreatedDate(Long servicePlanCreatedDate) {
        this.servicePlanCreatedDate = servicePlanCreatedDate;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAuditPerson() {
        return auditPerson;
    }

    public void setAuditPerson(String auditPerson) {
        this.auditPerson = auditPerson;
    }

    public Long getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Long checkDate) {
        this.checkDate = checkDate;
    }

    public Long getNextCheckDate() {
        return nextCheckDate;
    }

    public void setNextCheckDate(Long nextCheckDate) {
        this.nextCheckDate = nextCheckDate;
    }

    public Boolean getServiceProvided() {
        return serviceProvided;
    }

    public void setServiceProvided(Boolean serviceProvided) {
        this.serviceProvided = serviceProvided;
    }

    public boolean getCanViewServicePlan() {
        return canViewServicePlan;
    }

    public void setCanViewServicePlan(boolean canViewServicePlan) {
        this.canViewServicePlan = canViewServicePlan;
    }
}
