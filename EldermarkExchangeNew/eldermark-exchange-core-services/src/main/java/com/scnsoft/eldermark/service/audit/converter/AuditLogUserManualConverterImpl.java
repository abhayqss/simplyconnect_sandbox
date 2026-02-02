package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class AuditLogUserManualConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> userManualActivitiesWithNote = List.of(
            AuditLogActivity.USER_MANUAL_CREATE,
            AuditLogActivity.USER_MANUAL_DOWNLOAD
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedAdditionalFields)) {
            var userManualTitle = relatedAdditionalFields.get(0);
            if (userManualActivitiesWithNote.contains(activity)) {
                return List.of(userManualTitle);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.USER_MANUAL;
    }
}
