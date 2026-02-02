package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.projection.NamesCareTeamRoleNameAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.ContactService;
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
public class AuditLogContactConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> contactActivitiesWithNote = List.of(
            AuditLogActivity.CONTACT_VIEW,
            AuditLogActivity.CONTACT_EDIT,
            AuditLogActivity.CONTACT_CREATE,
            AuditLogActivity.CONTACT_INACTIVE,
            AuditLogActivity.CONTACT_REINVITE,
            AuditLogActivity.CONTACT_INVITE_ACCEPTED
    );

    @Autowired
    private ContactService contactService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (contactActivitiesWithNote.contains(activity)) {
                var contactAware = contactService.findById(relatedId, NamesCareTeamRoleNameAware.class);
                if (auditLog.isMobile()) {
                    return List.of("Mobile app, " + contactAware.getFullName());
                }
                return List.of(contactAware.getFullName() + " " + contactAware.getCareTeamRoleName());
            }
        }

        if (auditLog.isMobile()) {
            if (activity == AuditLogActivity.CONTACT_VIEW_LISTING) {
                return List.of("Staff in Mobile app");
            }
            return List.of("Mobile app");
        }
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.CONTACT;
    }
}
