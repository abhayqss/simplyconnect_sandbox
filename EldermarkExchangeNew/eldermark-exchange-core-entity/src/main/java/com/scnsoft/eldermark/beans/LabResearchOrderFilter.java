package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderReason;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

public class LabResearchOrderFilter {


    private Long clientId;
    private String requisitionNumber;
    private List<LabResearchOrderStatus> statuses;
    private List<LabResearchOrderReason> reasons;
    private List<Long> communityIds;
    @NotNull
    private Long organizationId;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public List<LabResearchOrderReason> getReasons() {
        return reasons;
    }

    public void setReasons(List<LabResearchOrderReason> reasons) {
        this.reasons = reasons;
    }

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

    public List<LabResearchOrderStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<LabResearchOrderStatus> statuses) {
        this.statuses = statuses;
    }
}
