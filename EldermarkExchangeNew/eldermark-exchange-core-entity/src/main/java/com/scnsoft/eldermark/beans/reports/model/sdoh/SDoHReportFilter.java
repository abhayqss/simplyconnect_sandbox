package com.scnsoft.eldermark.beans.reports.model.sdoh;

import java.time.Instant;

public class SDoHReportFilter {

    private Long organizationId;
    private Instant instantFrom;
    private Instant instantTo;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Instant getInstantFrom() {
        return instantFrom;
    }

    public void setInstantFrom(Instant instantFrom) {
        this.instantFrom = instantFrom;
    }

    public Instant getInstantTo() {
        return instantTo;
    }

    public void setInstantTo(Instant instantTo) {
        this.instantTo = instantTo;
    }
}
