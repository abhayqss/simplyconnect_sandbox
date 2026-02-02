package com.scnsoft.eldermark.dto.serviceplan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.*;

public class ServicePlanGoalItemDto {
    private Long id;

    @NotEmpty
    @Size(max = 256)
    private String goal;

    @Size(max = 5000)
    private String barriers;

    @Size(max = 5000)
    private String interventionAction;

    @Size(max = 256)
    private String resourceName;



    private Long targetCompletionDate;
    private Long completionDate;

    @Min(1)
    @Max(100)
    private Integer goalCompletion;

    @Size(max = 256)
    private String providerName;

    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    @Size(max = 256)
    private String providerEmail;

    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String providerPhone;

    @Size(max = 256)
    private String providerAddress;

    @JsonProperty("isOngoingService")
    private boolean ongoingService;

    @Size(max = 256)
    private String contactName;

    private Long serviceCtrlReqStatusId;

    private Long serviceStatusId;

    private Boolean wasPreviouslyInPlace;

    public Integer getGoalCompletion() {
        return goalCompletion;
    }

    public void setGoalCompletion(Integer goalCompletion) {
        this.goalCompletion = goalCompletion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getBarriers() {
        return barriers;
    }

    public void setBarriers(String barriers) {
        this.barriers = barriers;
    }

    public String getInterventionAction() {
        return interventionAction;
    }

    public void setInterventionAction(String interventionAction) {
        this.interventionAction = interventionAction;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Long targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public Long getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Long completionDate) {
        this.completionDate = completionDate;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public boolean isOngoingService() {
        return ongoingService;
    }

    public void setOngoingService(boolean ongoingService) {
        this.ongoingService = ongoingService;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Long getServiceCtrlReqStatusId() {
        return serviceCtrlReqStatusId;
    }

    public void setServiceCtrlReqStatusId(Long serviceCtrlReqStatusId) {
        this.serviceCtrlReqStatusId = serviceCtrlReqStatusId;
    }

    public Long getServiceStatusId() {
        return serviceStatusId;
    }

    public void setServiceStatusId(Long serviceStatusId) {
        this.serviceStatusId = serviceStatusId;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }

    public Boolean getWasPreviouslyInPlace() {
        return wasPreviouslyInPlace;
    }

    public void setWasPreviouslyInPlace(Boolean wasPreviouslyInPlace) {
        this.wasPreviouslyInPlace = wasPreviouslyInPlace;
    }
}
