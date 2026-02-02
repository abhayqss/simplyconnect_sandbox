package com.scnsoft.eldermark.service.report;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SdohReportLogService {

    Page<SdohReportLog> find(Long organizationId, PermissionFilter permissionFilter, Pageable pageable);

    Pair<String, byte[]> getZip(Long sdohReportLogId, PermissionFilter permissionFilter);

    Pair<String, byte[]> getExcel(Long sdohReportLogId, PermissionFilter permissionFilter);

    void markAsSentToUhc(Long sdohReportLogId, PermissionFilter permissionFilter);

    void validateCanMarkAsSentToUhc(Long sdohReportLogId, PermissionFilter permissionFilter);
}
