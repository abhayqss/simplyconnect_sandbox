package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AuditLog;
import com.scnsoft.eldermark.entity.AuditLogAction;

import java.util.List;

public interface AuditLoggingDao {
    public void logOperation(AuditLog entry);

    public void logOperation(AuditLogAction action, Long employeeId, List<Long> residentIds, List<Long> documentIds);
}