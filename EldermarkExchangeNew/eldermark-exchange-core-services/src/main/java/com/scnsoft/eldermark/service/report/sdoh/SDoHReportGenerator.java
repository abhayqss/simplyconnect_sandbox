package com.scnsoft.eldermark.service.report.sdoh;

import com.scnsoft.eldermark.beans.reports.model.sdoh.SDoHReport;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import com.scnsoft.eldermark.entity.sdoh.SdohReportRowData;

import java.util.List;

public interface SDoHReportGenerator {
    SDoHReport generateReport(SdohReportLog reportLog, PermissionFilter permissionFilter);

    SDoHReport restoreSentReport(SdohReportLog reportLog, PermissionFilter permissionFilter);

    List<SdohReportRowData> createRowData(SDoHReport report);
}
