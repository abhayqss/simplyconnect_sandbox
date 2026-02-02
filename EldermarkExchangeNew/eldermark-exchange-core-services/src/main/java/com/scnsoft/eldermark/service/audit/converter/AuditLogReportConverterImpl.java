package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.projection.DisplayNameAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import com.scnsoft.eldermark.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class AuditLogReportConverterImpl implements AuditLogBaseConverter<ReportType> {

    private static final List<AuditLogActivity> reportActivitiesWithNote = List.of(
            AuditLogActivity.REPORT_EXPORT
    );

    @Autowired
    private ReportService reportService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<ReportType> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (reportActivitiesWithNote.contains(activity)) {
                var reportConfigurationAware = reportService.findConfigurationByType(relatedId, DisplayNameAware.class);
                return List.of(reportConfigurationAware.getDisplayName());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.REPORT;
    }
}
