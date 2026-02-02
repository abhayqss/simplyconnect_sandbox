package com.scnsoft.eldermark.beans.reports.model;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;

import java.time.Instant;
import java.util.List;

public class Report {

    private ReportType reportType;

    private List<String> communityNames;

    private Instant dateFrom;

    private Instant dateTo;

    private Integer timeZoneOffset;

    public Instant getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Instant dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Instant getDateTo() {
        return dateTo;
    }

    public void setDateTo(Instant dateTo) {
        this.dateTo = dateTo;
    }

    public List<String> getCommunityNames() {
        return communityNames;
    }

    public void setCommunityNames(List<String> communityNames) {
        this.communityNames = communityNames;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }
}
