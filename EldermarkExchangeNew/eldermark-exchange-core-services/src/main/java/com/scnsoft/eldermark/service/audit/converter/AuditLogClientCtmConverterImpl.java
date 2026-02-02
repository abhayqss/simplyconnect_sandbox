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
public class AuditLogClientCtmConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> clientCTMActivitiesWithNote = List.of(
            AuditLogActivity.CLIENT_CARE_TEAM_VIEW,
            AuditLogActivity.CLIENT_CARE_TEAM_CREATE,
            AuditLogActivity.CLIENT_CARE_TEAM_DELETE,
            AuditLogActivity.CLIENT_CARE_TEAM_EDIT
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (auditLog.isMobile()) {
            return List.of("Mobile app");
        }

        if (!CollectionUtils.isEmpty(relatedAdditionalFields)) {
            var careTeamMemberFullName = relatedAdditionalFields.get(0);

            if (clientCTMActivitiesWithNote.contains(activity)) {
                return List.of("CTM name: " + careTeamMemberFullName);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.CLIENT_CTM;
    }
}
