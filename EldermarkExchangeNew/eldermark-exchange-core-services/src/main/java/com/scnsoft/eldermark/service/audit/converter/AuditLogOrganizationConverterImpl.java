package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.projection.NameAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.OrganizationService;
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
public class AuditLogOrganizationConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> organizationActivitiesWithNote = List.of(
            AuditLogActivity.ORGANIZATION_VIEW,
            AuditLogActivity.ORGANIZATION_EDIT,
            AuditLogActivity.ORGANIZATION_CREATE
    );

    @Autowired
    private OrganizationService organizationService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (organizationActivitiesWithNote.contains(activity)) {
                return List.of("Organization name " + organizationService.findById(relatedId, NameAware.class).getName());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.ORGANIZATION;
    }
}
