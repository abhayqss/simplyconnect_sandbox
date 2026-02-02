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
public class AuditLogCommunityCtmConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> communityCTMActivitiesWithNote = List.of(
            AuditLogActivity.COMMUNITY_CARE_TEAM_MEMBER_CREATE,
            AuditLogActivity.COMMUNITY_CARE_TEAM_MEMBER_EDIT,
            AuditLogActivity.COMMUNITY_CARE_TEAM_MEMBER_DELETE
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedAdditionalFields)) {
            var communityName = relatedAdditionalFields.get(0);
            var careTeamFullName = relatedAdditionalFields.get(1);

            if (communityCTMActivitiesWithNote.contains(activity)) {
                return List.of("Community name " + communityName, "CTM name: " + careTeamFullName);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.COMMUNITY_CTM;
    }
}
