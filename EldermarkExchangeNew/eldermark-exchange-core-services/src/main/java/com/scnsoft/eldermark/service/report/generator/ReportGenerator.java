package com.scnsoft.eldermark.service.report.generator;


import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.Report;
import com.scnsoft.eldermark.beans.security.PermissionFilter;

public interface ReportGenerator<R extends Report> {

    R generateReport(InternalReportFilter filter, PermissionFilter permissionFilter);

    ReportType getReportType();

}

