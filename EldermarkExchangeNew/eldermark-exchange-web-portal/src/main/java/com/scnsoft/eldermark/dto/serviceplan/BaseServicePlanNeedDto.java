package com.scnsoft.eldermark.dto.serviceplan;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public abstract class BaseServicePlanNeedDto {

    @NotNull
    private Long domainId;

    private String domainName;

    @NotNull
    private Long priorityId;

    @Size(max = 20000)
    private String needOpportunity;

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public String getNeedOpportunity() {
        return needOpportunity;
    }

    public void setNeedOpportunity(String needOpportunity) {
        this.needOpportunity = needOpportunity;
    }
}
