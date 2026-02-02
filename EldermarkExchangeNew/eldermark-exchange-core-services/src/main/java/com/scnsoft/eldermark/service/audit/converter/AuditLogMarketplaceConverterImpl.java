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
public class AuditLogMarketplaceConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> marketplaceActivitiesWithNote = List.of(
            AuditLogActivity.MARKETPLACE_VIEW_COMMUNITY_DETAILS,
            AuditLogActivity.MARKETPLACE_VIEW_PARTNER_PROVIDERS
    );

    @Autowired
    private CommunityService communityService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (auditLog.isMobile()) {
            return List.of("Mobile app");
        }

        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (marketplaceActivitiesWithNote.contains(activity)) {
                var aware = communityService.findById(relatedId, NameAware.class);
                var result = "Community: " + aware.getName() + "\n ";
                return List.of(result);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.MARKETPLACE;
    }
}
