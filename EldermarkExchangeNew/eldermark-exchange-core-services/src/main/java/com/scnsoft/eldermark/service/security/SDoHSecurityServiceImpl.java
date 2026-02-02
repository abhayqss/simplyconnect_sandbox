package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.SdohReportLogSecurityAwareEntity;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.SdohReportLogDao;
import com.scnsoft.eldermark.dao.specification.SdohReportLogSpecificationGenerator;
import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("sDoHSecurityService")
@Transactional
public class SDoHSecurityServiceImpl extends BaseSecurityService implements SDoHSecurityService {

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private SdohReportLogDao sdohReportLogDao;

    @Autowired
    private SdohReportLogSpecificationGenerator sdohSpecificationGenerator;

    @Override
    public boolean canView(Long reportId) {
        return hasCommonAccess(reportId, Permission.SDOH_VIEW_IF_ASSOCIATED_ORGANIZATION);
    }

    @Override
    public boolean canViewInOrganization(Long organizationId) {
        return hasCommonAccessInOrganization(organizationId, Permission.SDOH_VIEW_IF_ASSOCIATED_ORGANIZATION);
    }

    @Override
    public boolean canView() {
        var filter = currentUserFilter();
        var canViewInOrg = sdohSpecificationGenerator.canViewInOrganization(filter);
        return organizationDao.count(canViewInOrg) > 0;
    }

    @Override
    public boolean canDownloadExcel(Long reportId) {
        return hasCommonAccess(reportId, Permission.SDOH_DOWNLOAD_EXCEL_IF_ASSOCIATED_ORGANIZATION);
    }

    @Override
    public boolean canDownloadZip(Long reportId) {
        return hasCommonAccess(reportId, Permission.SDOH_DOWNLOAD_ZIP_IF_ASSOCIATED_ORGANIZATION);
    }

    @Override
    public boolean canMarkAsSent(Long reportId) {
        return hasCommonAccess(reportId, Permission.SDOH_MARK_AS_SENT_IF_ASSOCIATED_ORGANIZATION);
    }

    private boolean hasCommonAccess(Long reportId, Permission associatedOrganization) {
        var report = sdohReportLogDao.findById(reportId, SdohReportLogSecurityAwareEntity.class).orElseThrow();
        return hasCommonAccessInOrganization(report.getOrganizationId(), associatedOrganization);
    }

    private boolean hasCommonAccessInOrganization(Long organizationId, Permission associatedOrganization) {
        var filter = currentUserFilter();

        var organization = organizationDao.findById(organizationId, OrganizationSecurityAwareEntity.class).orElseThrow();
        if (!organization.isSdohReportsEnabled()) {
            return false;
        }

        if (filter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (filter.hasPermission(associatedOrganization)) {
            var employees = filter.getEmployees(associatedOrganization);
            if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                return true;
            }
        }

        return true;
    }
}
