package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.intune.InTuneReport;
import com.scnsoft.eldermark.beans.reports.model.intune.InTuneReportClientInfo;
import com.scnsoft.eldermark.beans.security.PermissionFilter;

public interface InTuneReportGenerator extends ReportGenerator<InTuneReport> {

    InTuneReportClientInfo getClientInfo(Long clientId, PermissionFilter permissionFilter);

    InTuneReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter);

    InTuneReport generateSingleClientReport(Long clientId, PermissionFilter permissionFilter);

    default ReportType getReportType() {
        return ReportType.IN_TUNE;
    }
}

