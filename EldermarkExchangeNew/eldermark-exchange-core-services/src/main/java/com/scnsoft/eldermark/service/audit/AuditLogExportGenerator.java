package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogFilterDto;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Collection;

public interface AuditLogExportGenerator {
    Workbook generate(Collection<AuditLog> auditLogs, AuditLogFilterDto filter);
}
