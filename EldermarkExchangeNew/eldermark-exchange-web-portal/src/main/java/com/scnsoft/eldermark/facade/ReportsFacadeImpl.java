package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.reports.filter.ReportFilter;
import com.scnsoft.eldermark.dto.ReportTypeDto;
import com.scnsoft.eldermark.dto.report.ReportFilterDto;
import com.scnsoft.eldermark.entity.report.ReportConfiguration;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.report.ReportService;
import com.scnsoft.eldermark.service.report.converter.ReportToWorkbookConverter;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.validation.ReportsValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.service.report.converter.WriterUtils.copyDocumentContentToResponse;
import static com.scnsoft.eldermark.util.DateTimeUtils.toLocalDate;

@Service
public class ReportsFacadeImpl implements ReportsFacade {

    @Autowired
    private ReportService reportService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ReportsValidationService reportsValidationService;

    @Autowired
    private ReportToWorkbookConverter reportConverter;

    @Override
    @PreAuthorize("@reportSecurityService.canGenerateForCommunities(#filterDto.reportType, #filterDto.communityIds)")
    public void downloadReport(ReportFilterDto filterDto, HttpServletResponse response) {
        var timezoneOffset = filterDto.getTimezoneOffset();
        var filter = convertReportFilterDto(filterDto);
        reportsValidationService.validateFilter(filter);

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        var report = reportService.generateReport(filter, permissionFilter);
        report.setTimeZoneOffset(timezoneOffset);
        report.setReportType(filter.getReportType());
        var workbook = reportConverter.convert(report);

        copyDocumentContentToResponse(workbook, buildFileName(filterDto), response);
    }

    @Override
    @PreAuthorize("@reportSecurityService.canGenerate()")
    public List<ReportTypeDto> getAvailableReportTypes(Long organizationId, List<Long> communityIds) {

        if (organizationId == null && CollectionUtils.isEmpty(communityIds)) {
            throw new ValidationException("At least one of the parameters: organizationId, communityIds shouldn't be empty");
        }

        var configs = CollectionUtils.isEmpty(communityIds)
                ? reportService.findAllConfigurationsAvailableInOrganization(organizationId)
                : reportService.findAllConfigurationsAvailableInAnyCommunity(communityIds);

        return configs.stream()
                .sorted(Comparator.comparing(ReportConfiguration::getDisplayName))
                .map(it -> new ReportTypeDto(it.getType().name(), it.getDisplayName()))
                .collect(Collectors.toList());
    }

    private ReportFilter convertReportFilterDto(ReportFilterDto source) {
        var target = new ReportFilter();
        target.setReportType(source.getReportType());
        target.setFromDate(source.getFromDate());
        target.setToDate(source.getToDate());
        target.setCommunityIds(source.getCommunityIds());
        target.setTimezoneOffset(source.getTimezoneOffset());
        return target;
    }

    private String buildFileName(ReportFilterDto filter) {
        var name = filter.getReportType() + "_" + toLocalDate(Instant.ofEpochMilli(filter.getFromDate()), filter.getTimezoneOffset());
        if (filter.getToDate() != null) {
            name += "---" + toLocalDate(Instant.ofEpochMilli(filter.getToDate()), filter.getTimezoneOffset());
        }
        return name;
    }
}
