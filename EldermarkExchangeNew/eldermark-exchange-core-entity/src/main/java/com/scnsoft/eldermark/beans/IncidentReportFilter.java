package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.IncidentReportStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

public class IncidentReportFilter {

    @NotNull
    private Long organizationId;
    private List<Long> communityIds;
    private List<IncidentReportStatus> statuses;
    private Long clientId;
    private Long fromDate;
    private Long toDate;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public List<IncidentReportStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<IncidentReportStatus> statuses) {
        this.statuses = statuses;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }
}
