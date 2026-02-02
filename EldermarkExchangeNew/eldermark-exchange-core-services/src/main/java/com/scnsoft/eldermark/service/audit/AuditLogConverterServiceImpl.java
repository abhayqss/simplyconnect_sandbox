package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
@Transactional(readOnly = true)
public class AuditLogConverterServiceImpl<T extends Object & Serializable, A> implements AuditLogConverterService<T, A> {

    private final Map<AuditLogType, AuditLogBaseConverter<T>> converterMap;

    @Autowired
    public AuditLogConverterServiceImpl(List<AuditLogBaseConverter<T>> converters) {
        this.converterMap = converters.stream().collect(toMap(AuditLogBaseConverter::getConverterType, Function.identity()));
    }

    @Override
    public AuditLogActivity convertToAuditLogActivity(A auditLogType, AuditLog auditLog, List<T> relatedIds) {
        if (auditLogType == null) {
            return AuditLogActivity.getByAuditLogAction(auditLog.getAction());
        }
        return converterMap.get(auditLogType).convertToAuditLogActivity(auditLog, relatedIds);

    }

    @Override
    public List<String> convertNotes(A auditLogType, AuditLogActivity activity, AuditLog auditLog, List<T> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (auditLogType == null) {
            var logType = AuditLogType.getByAuditLogActivity(activity);
            return logType != null
                    ? converterMap.get(logType).convertNotes(activity, auditLog, relatedIds, relatedAdditionalFields, zoneId)
                    : List.of("");
        }
        return converterMap.get(auditLogType).convertNotes(activity, auditLog, relatedIds, relatedAdditionalFields, zoneId);
    }
}
