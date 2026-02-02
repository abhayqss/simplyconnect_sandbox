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
public class AuditLogSignatureTemplateConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> signatureTemplateActivitiesWithNote = List.of(
            AuditLogActivity.ESIGN_BUILDER_TEMPLATE_CREATE,
            AuditLogActivity.ESIGN_BUILDER_TEMPLATE_UPDATE,
            AuditLogActivity.ESIGN_BUILDER_TEMPLATE_DELETE
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedAdditionalFields)) {
            var templateTitle = relatedAdditionalFields.get(0);

            if (signatureTemplateActivitiesWithNote.contains(activity)) {
                return List.of(templateTitle);
            }
        }

        if (auditLog.isMobile()) {
            return List.of("Mobile app");
        }
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SIGNATURE_TEMPLATE;
    }
}
