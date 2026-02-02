package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.entity.event.EventDateTimeTypeAware;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class AuditLogEventConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> eventActivitiesWithNote = List.of(
            AuditLogActivity.EVENT_VIEW,
            AuditLogActivity.EVENT_CREATE
    );

    @Autowired
    private EventService eventService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (eventActivitiesWithNote.contains(activity)) {
                var eventAware = eventService.findById(relatedId, EventDateTimeTypeAware.class);
                return List.of("Event date & time " + DateTimeUtils.formatDateTime(eventAware.getEventDateTime(), zoneId), eventAware.getEventType().getDescription());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.EVENT;
    }
}
