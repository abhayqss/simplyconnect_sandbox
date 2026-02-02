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
public class AuditLogReleaseNoteConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> releaseNoteActivitiesWithNote = List.of(
            AuditLogActivity.RELEASE_NOTE_CREATE,
            AuditLogActivity.RELEASE_NOTE_DOWNLOAD
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedAdditionalFields)) {
            var releaseNoteTitle = relatedAdditionalFields.get(0);

            if (releaseNoteActivitiesWithNote.contains(activity)) {
                return List.of(releaseNoteTitle);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.RELEASE_NOTE;
    }
}
