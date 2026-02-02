package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

public interface AuditLogBaseConverter<T> {

    default AuditLogActivity convertToAuditLogActivity(AuditLog auditLog, List<T> relatedIds) {
        return AuditLogActivity.getByAuditLogAction(auditLog.getAction());
    }

    default List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<T> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId){
        return Collections.emptyList();
    }

    AuditLogType getConverterType();

}
