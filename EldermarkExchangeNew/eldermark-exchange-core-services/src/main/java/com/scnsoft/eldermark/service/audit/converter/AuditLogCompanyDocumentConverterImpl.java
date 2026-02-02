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
public class AuditLogCompanyDocumentConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> companyDocumentActivitiesWithNote = List.of(
            AuditLogActivity.COMPANY_DOCUMENT_UPLOAD,
            AuditLogActivity.COMPANY_DOCUMENT_DOWNLOAD
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedAdditionalFields)) {
            if (companyDocumentActivitiesWithNote.contains(activity)) {
                return relatedAdditionalFields;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.COMPANY_DOCUMENT;
    }
}
