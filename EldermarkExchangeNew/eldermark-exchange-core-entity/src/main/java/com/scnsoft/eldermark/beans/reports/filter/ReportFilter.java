package com.scnsoft.eldermark.beans.reports.filter;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;

import java.util.List;

public class ReportFilter {

    private List<Long> communityIds;

    private Long fromDate;

    private Long toDate;

    private ReportType reportType;

    private Integer timezoneOffset;

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
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
