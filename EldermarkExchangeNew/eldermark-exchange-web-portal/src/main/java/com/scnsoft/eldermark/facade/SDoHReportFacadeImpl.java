package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.SDoHReportListItemDto;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import com.scnsoft.eldermark.service.report.SdohReportLogService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.security.SDoHSecurityService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
@Transactional
public class SDoHReportFacadeImpl implements SDoHReportFacade {

    @Autowired
    private SdohReportLogService sdohService;

    @Autowired
    private Converter<SdohReportLog, SDoHReportListItemDto> listItemDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private SDoHSecurityService sDoHSecurityService;

    @Override
    @PreAuthorize("@sDoHSecurityService.canViewInOrganization(#organizationId)")
    public Page<SDoHReportListItemDto> find(@P("organizationId") Long organizationId, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        pageable = PaginationUtils.applyEntitySort(pageable, SDoHReportListItemDto.class);

        return sdohService.find(organizationId, permissionFilter, pageable)
                .map(listItemDtoConverter::convert);
    }

    @Override
    @PreAuthorize("@sDoHSecurityService.canDownloadZip(#reportId)")
    public void downloadZip(@P("reportId") Long reportId, HttpServletResponse response) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var zip = sdohService.getZip(reportId, permissionFilter);

        WriterUtils.copyDocumentContentToResponse(zip.getFirst(), zip.getSecond(), "application/zip", false, response);

    }

    @Override
    @PreAuthorize("@sDoHSecurityService.canDownloadExcel(#reportId)")
    public void downloadXlsx(@P("reportId") Long reportId, HttpServletResponse response) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var excel = sdohService.getExcel(reportId, permissionFilter);

        WriterUtils.copyDocumentContentToResponse(excel.getFirst(), excel.getSecond(), WriterUtils.XSLX_MIME_TYPE, false, response);
    }

    @Override
    @PreAuthorize("@sDoHSecurityService.canMarkAsSent(#reportId)")
    public void markAsSent(@P("reportId") Long reportId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        sdohService.markAsSentToUhc(reportId, permissionFilter);
    }

    @Override
    @PreAuthorize("@sDoHSecurityService.canMarkAsSent(#reportId)")
    public boolean canMarkAsSent(@P("reportId") Long reportId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        sdohService.validateCanMarkAsSentToUhc(reportId, permissionFilter);
        return true;
    }

    @Override
    public boolean canView() {
        return sDoHSecurityService.canView();
    }
}
