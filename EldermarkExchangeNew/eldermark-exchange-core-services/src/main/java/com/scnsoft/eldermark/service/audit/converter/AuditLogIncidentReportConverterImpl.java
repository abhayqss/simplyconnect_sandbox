package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.entity.event.incident.IncidentDateTimeAware;
import com.scnsoft.eldermark.service.IncidentReportService;
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
public class AuditLogIncidentReportConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> incidentReportActivitiesWithNote = List.of(
            AuditLogActivity.INCIDENT_REPORT_VIEW,
            AuditLogActivity.INCIDENT_REPORT_DOWNLOAD,
            AuditLogActivity.INCIDENT_REPORT_EDIT,
            AuditLogActivity.INCIDENT_REPORT_CREATE
    );

    @Autowired
    private IncidentReportService incidentReportService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (incidentReportActivitiesWithNote.contains(activity)) {
                return List.of("Incident date " + DateTimeUtils.formatDate(incidentReportService.findById(relatedId, IncidentDateTimeAware.class).getIncidentDatetime(), zoneId));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.INCIDENT_REPORT;
    }
}
