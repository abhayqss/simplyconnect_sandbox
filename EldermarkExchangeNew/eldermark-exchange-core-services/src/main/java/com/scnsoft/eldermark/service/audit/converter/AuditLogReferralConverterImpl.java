package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.ReferralService;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Transactional(readOnly = true)
public class AuditLogReferralConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> referralActivitiesWithNote = List.of(
            AuditLogActivity.REFERRAL_OUTBOUND_VIEW,
            AuditLogActivity.REFERRAL_CREATE,
            AuditLogActivity.REFERRAL_REQUEST_CANCEL
    );

    @Autowired
    private ReferralService referralService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (referralActivitiesWithNote.contains(activity)) {
                return Stream.ofNullable(referralService.findById(relatedId).getReferralRequestIds())
                        .flatMap(Set::stream)
                        .map(id -> "Request ID " + id)
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.REFERRAL;
    }
}
