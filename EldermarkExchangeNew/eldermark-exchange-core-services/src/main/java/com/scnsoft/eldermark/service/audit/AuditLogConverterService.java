package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.List;

public interface AuditLogConverterService<T extends Object & Serializable, A> {

    AuditLogActivity convertToAuditLogActivity(A auditLogType, AuditLog auditLog, List<T> relatedIds);

    List<String> convertNotes(A auditLogType, AuditLogActivity activity, AuditLog auditLog, List<T> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId);
}
