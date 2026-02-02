package com.scnsoft.eldermark.service.report;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.ReportFilter;
import com.scnsoft.eldermark.beans.reports.model.Report;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.report.ReportConfiguration;

import java.util.Collection;
import java.util.List;

public interface ReportService {

    Report generateReport(ReportFilter filter, PermissionFilter permissionFilter);

    ReportConfiguration findConfigurationByType(ReportType reportType);

    <P> P findConfigurationByType(ReportType reportType, Class<P> projectionClass);

    List<ReportConfiguration> findAllConfigurationsAvailableInOrganization(Long organizationId);

    List<ReportConfiguration> findAllConfigurationsAvailableInAnyCommunity(Collection<Long> communityIds);

    boolean isReportAvailableInAnyCommunity(ReportType reportType, Collection<Long> communityIds);
}
