package com.scnsoft.eldermark.service.report;

import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.filter.ReportFilter;
import com.scnsoft.eldermark.beans.reports.model.Report;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.report.ReportConfigurationDao;
import com.scnsoft.eldermark.dao.specification.CommunitySpecificationGenerator;
import com.scnsoft.eldermark.entity.report.ReportConfiguration;
import com.scnsoft.eldermark.service.AssessmentService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.report.generator.ReportGenerator;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final Map<ReportType, ReportGenerator<?>> generatorMap;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunitySpecificationGenerator communitySpecificationGenerator;

    @Autowired
    private ReportConfigurationDao reportConfigurationDao;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    public ReportServiceImpl(List<ReportGenerator<?>> generators) {
        this.generatorMap = generators.stream().collect(toMap(ReportGenerator::getReportType, Function.identity()));
    }

    @Override
    public Report generateReport(ReportFilter filter, PermissionFilter permissionFilter) {
        var internalReportFilter = buildInternalReportFilter(filter, permissionFilter);
        return generatorMap.get(filter.getReportType()).generateReport(internalReportFilter, permissionFilter);
    }

    @Override
    public ReportConfiguration findConfigurationByType(ReportType reportType) {
        return reportConfigurationDao.findById(reportType).orElseThrow();
    }

    @Override
    public <P> P findConfigurationByType(ReportType reportType, Class<P> projectionClass) {
        return reportConfigurationDao.findById(reportType, projectionClass).orElseThrow();
    }

    @Override
    public List<ReportConfiguration> findAllConfigurationsAvailableInOrganization(Long organizationId) {
        return findAllConfigurationsAvailableInAnyCommunity(
                CareCoordinationUtils.toIdsSet(communityService.findAllByOrgId(organizationId))
        );
    }

    @Override
    public List<ReportConfiguration> findAllConfigurationsAvailableInAnyCommunity(Collection<Long> communityIds) {
        var configs = reportConfigurationDao.findAll();
        var communities = communityService.findSecurityAwareEntities(communityIds);
        return configs.stream()
                .filter(config -> isReportAvailableInAnyCommunity(config, communities))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isReportAvailableInAnyCommunity(ReportType reportType, Collection<Long> communityIds) {
        var reportConfig = reportConfigurationDao.findById(reportType).orElseThrow();
        var communities = communityService.findSecurityAwareEntities(communityIds);
        return isReportAvailableInAnyCommunity(reportConfig, communities);
    }

    private InternalReportFilter buildInternalReportFilter(ReportFilter reportFilter, PermissionFilter permissionFilter) {
        var internalReportFilter = new InternalReportFilter();

        internalReportFilter.setReportType(reportFilter.getReportType());
        internalReportFilter.setAccessibleCommunityIdsAndNames(accessibleCommunities(reportFilter, permissionFilter));
        internalReportFilter.setInstantFrom(DateTimeUtils.toInstant(reportFilter.getFromDate()));
        internalReportFilter.setInstantTo(DateTimeUtils.toInstant(reportFilter.getToDate()));
        internalReportFilter.setTimezoneOffset(reportFilter.getTimezoneOffset());
        return internalReportFilter;
    }

    protected List<IdNameAware> accessibleCommunities(ReportFilter reportFilter, PermissionFilter permissionFilter) {

        var hasAccess = communitySpecificationGenerator.hasAccess(permissionFilter);
        var byIds = communitySpecificationGenerator.byCommunityIdsEligibleForDiscovery(reportFilter.getCommunityIds());

        var communities = communityDao.findAll(byIds.and(hasAccess), CommunityReportDetailsAware.class);

        var reportConfig = reportConfigurationDao.findById(reportFilter.getReportType()).orElseThrow();

        return communities.stream()
                .filter(community -> isReportAvailableInCommunity(reportConfig, community))
                .map(it -> (IdNameAware) it)
                .collect(Collectors.toList());
    }

    private boolean isReportAvailableInCommunity(ReportConfiguration report, CommunitySecurityAwareEntity community) {
        if (report.getDependsOnAssessment() != null) {
            if (!assessmentService.isTypeAllowedForCommunity(community, report.getDependsOnAssessment())) {
                return false;
            }
        }

        if (report.isShared()) {
            return !report.getDisabledCommunityIds().contains(community.getId())
                    && !report.getDisabledOrganizationIds().contains(community.getOrganizationId());
        } else {
            return report.getEnabledCommunityIds().contains(community.getId())
                    || report.getEnabledOrganizationIds().contains(community.getOrganizationId());
        }
    }

    private boolean isReportAvailableInAnyCommunity(
            ReportConfiguration reportConfig,
            Collection<CommunitySecurityAwareEntity> communities
    ) {
        return communities.stream()
                .anyMatch(community -> isReportAvailableInCommunity(reportConfig, community));
    }

    private interface CommunityReportDetailsAware extends IdNameAware, CommunitySecurityAwareEntity {
    }
}
