package com.scnsoft.eldermark.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.validation.SpELAssert;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@SpELAssert(
    applyIf = "!reportType.name().equals('IN_TUNE')",
    value = "toDate != null",
    message = "toDate {javax.validation.constraints.NotNull.message}",
    helpers = StringUtils.class
)
public class ReportFilterDto {
    @NotEmpty
    private List<Long> communityIds;

    @NotNull
    private Long fromDate;

    private Long toDate;

    @JsonIgnore
    private ReportType reportType;

    @JsonIgnore
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

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }
}
