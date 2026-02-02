package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.projection.SupportTicketTypeTitleAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.SupportTicketService;
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
public class AuditLogSupportTicketConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> supportTicketActivitiesWithNote = List.of(
            AuditLogActivity.SUPPORT_TICKET_CREATE
    );

    @Autowired
    private SupportTicketService supportTicketService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (supportTicketActivitiesWithNote.contains(activity)) {
                var aware = supportTicketService.findById(relatedId, SupportTicketTypeTitleAware.class);
                return List.of(aware.getTypeTitle());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SUPPORT_TICKET;
    }
}
