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
public class AuditLogDocumentConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> documentActivitiesWithNote = List.of(
            AuditLogActivity.DOCUMENT_DOWNLOAD,
            AuditLogActivity.DOCUMENT_VIEW,
            AuditLogActivity.DOCUMENT_UPLOAD,
            AuditLogActivity.DOCUMENT_EDIT,
            AuditLogActivity.CCD_GENERATE_AND_VIEW,
            AuditLogActivity.CCD_GENERATE_AND_DOWNLOAD,
            AuditLogActivity.FACESHEET_GENERATE_AND_DOWNLOAD,
            AuditLogActivity.FACESHEET_GENERATE_AND_VIEW
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedAdditionalFields)) {
            var documentTitle = relatedAdditionalFields.get(0);
            if (auditLog.isMobile()) {
                if (AuditLogActivity.DOCUMENT_VIEW.equals(activity) || AuditLogActivity.DOCUMENT_DOWNLOAD.equals(activity)) {
                    return List.of(documentTitle + "\nMobile app");
                }
                return List.of("Mobile app");
            }

            if (documentActivitiesWithNote.contains(activity)) {
                return List.of(documentTitle);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.DOCUMENT;
    }
}
