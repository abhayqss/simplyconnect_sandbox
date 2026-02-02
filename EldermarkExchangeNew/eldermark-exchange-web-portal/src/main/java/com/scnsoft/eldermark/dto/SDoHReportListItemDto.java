package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog_;
import org.springframework.data.domain.Sort;

public class SDoHReportListItemDto {

    private Long id;

    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(SdohReportLog_.PERIOD_START)
    private Long periodStart;
    private Long periodEnd;

    @EntitySort(value = SdohReportLog_.SENT_TO_UHC_DATETIME)
    private String statusName;
    private String statusTitle;

    private boolean canDownloadZip;
    private boolean canDownloadExcel;
    private boolean canMarkAsSent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Long periodStart) {
        this.periodStart = periodStart;
    }

    public Long getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Long periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public boolean getCanDownloadZip() {
        return canDownloadZip;
    }

    public void setCanDownloadZip(boolean canDownloadZip) {
        this.canDownloadZip = canDownloadZip;
    }

    public boolean getCanDownloadExcel() {
        return canDownloadExcel;
    }

    public void setCanDownloadExcel(boolean canDownloadExcel) {
        this.canDownloadExcel = canDownloadExcel;
    }

    public boolean getCanMarkAsSent() {
        return canMarkAsSent;
    }

    public void setCanMarkAsSent(boolean canMarkAsSent) {
        this.canMarkAsSent = canMarkAsSent;
    }
}
