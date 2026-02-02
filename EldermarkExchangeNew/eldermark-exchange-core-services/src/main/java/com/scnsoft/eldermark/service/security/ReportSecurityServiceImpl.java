package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("reportSecurityService")
@Transactional(readOnly = true)
public class ReportSecurityServiceImpl extends BaseSecurityService implements ReportSecurityService {

    @Autowired
    private ReportService reportService;

    @Autowired
    private CommunityService communityService;

    @Override
    public boolean canGenerate() {
        var filter = currentUserFilter();
        return hasReportGenerationPermission(filter);
    }

    @Override
    public boolean canGenerateForCommunities(ReportType reportType, List<Long> communityIds) {
        var filter = currentUserFilter();

        if (!hasReportGenerationPermission(filter)) {
            return false;
        }

        return reportService.isReportAvailableInAnyCommunity(reportType, communityIds);
    }

    private boolean hasReportGenerationPermission(PermissionFilter filter) {
        return filter.hasPermission(Permission.REPORT_GENERATION_ALLOWED);
    }
}
