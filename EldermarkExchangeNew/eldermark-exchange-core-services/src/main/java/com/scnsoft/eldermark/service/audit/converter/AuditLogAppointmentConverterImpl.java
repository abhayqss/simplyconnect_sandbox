package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientAppointmentTitleAndDatesAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.ClientAppointmentService;
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
public class AuditLogAppointmentConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> appointmentActivitiesWithNote = List.of(
            AuditLogActivity.APPOINTMENT_CREATE,
            AuditLogActivity.APPOINTMENT_UPDATE,
            AuditLogActivity.APPOINTMENT_CANCEL,
            AuditLogActivity.APPOINTMENT_VIEW
    );
    @Autowired
    private ClientAppointmentService clientAppointmentService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (appointmentActivitiesWithNote.contains(activity)) {
                var aware = clientAppointmentService.findById(relatedId, ClientAppointmentTitleAndDatesAware.class);
                return List.of(aware.getTitle() + "\n"
                        + DateTimeUtils.formatDateTime(aware.getDateFrom(), zoneId) + " - "
                        + DateTimeUtils.formatDateTime(aware.getDateTo(), zoneId));
            }
        }

        if (auditLog.isMobile()) {
            return List.of("Mobile app");
        }
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.APPOINTMENT;
    }
}
