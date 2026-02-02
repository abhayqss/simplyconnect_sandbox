package com.scnsoft.eldermark.service.security;

public interface SDoHSecurityService {

    boolean canView(Long reportId);

    boolean canViewInOrganization(Long organizationId);

    boolean canView();

    boolean canDownloadExcel(Long reportId);

    boolean canDownloadZip(Long reportId);

    boolean canMarkAsSent(Long reportId);

}
