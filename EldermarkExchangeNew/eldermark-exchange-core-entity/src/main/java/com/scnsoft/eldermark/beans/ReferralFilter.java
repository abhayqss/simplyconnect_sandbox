package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.referral.ReferralStatus;

import java.util.List;

public class ReferralFilter {

    private List<Long> communityIds;
    private Long organizationId;
    private List<Long> serviceIds;
    private List<Long> priorityIds;
    private String source;
    private Long assignedTo;
    private List<Long> referredBy;
    private List<Long> referredTo;
    private List<ReferralStatus> statuses;
    private Long clientId;


    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Long> getPriorityIds() {
        return priorityIds;
    }

    public void setPriorityIds(List<Long> priorityIds) {
        this.priorityIds = priorityIds;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<ReferralStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<ReferralStatus> statuses) {
        this.statuses = statuses;
    }

    public List<Long> getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(List<Long> referredBy) {
        this.referredBy = referredBy;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<Long> getReferredTo() {
        return referredTo;
    }

    public void setReferredTo(List<Long> referredTo) {
        this.referredTo = referredTo;
    }
}
