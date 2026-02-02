package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.projection.NameAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class AuditLogCommunityConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> communityActivitiesWithNote = List.of(
            AuditLogActivity.COMMUNITY_VIEW,
            AuditLogActivity.COMMUNITY_EDIT,
            AuditLogActivity.COMMUNITY_CREATE
    );

    @Autowired
    private CommunityService communityService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (communityActivitiesWithNote.contains(activity)) {
                return List.of("Community name " + communityService.findById(relatedId, NameAware.class).getName());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.COMMUNITY;
    }
}