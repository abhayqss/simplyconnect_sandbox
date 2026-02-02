package com.scnsoft.eldermark.dto.notification.note;

public interface NoteServiceStatusCheckViewData {
    Long getServicePlanId();
    void setServicePlanId(Long servicePlanId);
    Long getServicePlanCreatedDate();
    void setServicePlanCreatedDate(Long servicePlanCreatedDate);
    String getResourceName();
    void setResourceName(String resourceName);
    String getProviderName();
    void setProviderName(String providerName);
    String getAuditPerson();
    void setAuditPerson(String auditPerson);
    Long getCheckDate();
    void setCheckDate(Long checkDate);
    Long getNextCheckDate();
    void setNextCheckDate(Long nextCheckDate);
    Boolean getServiceProvided();
    void setServiceProvided(Boolean serviceProvided);
    boolean getCanViewServicePlan();
    void setCanViewServicePlan(boolean canViewServicePlan);
}
