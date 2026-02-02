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
public class AuditLogReferralRequestConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> referralRequestActivitiesWithNote = List.of(
            AuditLogActivity.REFERRAL_INBOUND_VIEW,
            AuditLogActivity.REFERRAL_REQUEST_DECLINE,
            AuditLogActivity.REFERRAL_REQUEST_ACCEPT,
            AuditLogActivity.REFERRAL_REQUEST_PRE_ADMIT
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (referralRequestActivitiesWithNote.contains(activity)) {
                return List.of("Request ID " + relatedId);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.REFERRAL_REQUEST;
    }
}
