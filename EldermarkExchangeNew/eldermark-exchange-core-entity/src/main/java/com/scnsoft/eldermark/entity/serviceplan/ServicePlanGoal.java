package com.scnsoft.eldermark.entity.serviceplan;

import com.scnsoft.eldermark.entity.basic.ChainIdAware;
import com.scnsoft.eldermark.entity.basic.HistoryIdsAwareEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "ServicePlanGoal")
public class ServicePlanGoal extends HistoryIdsAwareEntity implements Serializable, ChainIdAware {

    @Column(name = "goal", nullable = false)
    private String goal;

    @Column(name = "barriers")
    private String barriers;

    @Column(name = "intervention_action")
    private String interventionAction;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "target_completion_date", nullable = false)
    private Instant targetCompletionDate;

    @Column(name = "completion_date")
    private Instant completionDate;

    @JoinColumn(name = "service_plan_goal_need_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private ServicePlanGoalNeed need;

    @Column(name = "goal_completion")
    private Integer goalCompletion;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "email")
    private String providerEmail;

    @Column(name = "phone")
    private String providerPhone;

    @Column(name = "is_ongoing")
    private Boolean ongoingService;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "provider_address")
    private String providerAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private ReferralServiceRequestStatus referralServiceRequestStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReferralServiceStatus referralServiceStatus;


    @Column(name = "was_previously_in_place")
    private Boolean wasPreviouslyInPlace;

    public Integer getGoalCompletion() {
        return goalCompletion;
    }

    public void setGoalCompletion(Integer goalCompletion) {
        this.goalCompletion = goalCompletion;
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

    public Instant getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Instant targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public Instant getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Instant completionDate) {
        this.completionDate = completionDate;
    }

    public ServicePlanGoalNeed getNeed() {
        return need;
    }

    public void setNeed(ServicePlanGoalNeed need) {
        this.need = need;
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

    public Boolean getOngoingService() {
        return ongoingService;
    }

    public void setOngoingService(Boolean ongoingService) {
        this.ongoingService = ongoingService;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public ReferralServiceRequestStatus getReferralServiceRequestStatus() {
        return referralServiceRequestStatus;
    }

    public void setReferralServiceRequestStatus(ReferralServiceRequestStatus referralServiceRequestStatus) {
        this.referralServiceRequestStatus = referralServiceRequestStatus;
    }

    public ReferralServiceStatus getReferralServiceStatus() {
        return referralServiceStatus;
    }

    public void setReferralServiceStatus(ReferralServiceStatus referralServiceStatus) {
        this.referralServiceStatus = referralServiceStatus;
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
