package com.scnsoft.eldermark.beans.reports.filter;

import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;

import java.time.Instant;
import java.util.List;

public class InternalReportFilter {

    private ReportType reportType;
    private List<IdNameAware> accessibleCommunityIdsAndNames;
    private Instant instantFrom;
    private Instant instantTo;
    private Integer timezoneOffset;

    public List<IdNameAware> getAccessibleCommunityIdsAndNames() {
        return accessibleCommunityIdsAndNames;
    }

    public void setAccessibleCommunityIdsAndNames(List<IdNameAware> accessibleCommunityIdsAndNames) {
        this.accessibleCommunityIdsAndNames = accessibleCommunityIdsAndNames;
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

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(final Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }
}
