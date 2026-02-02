package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.assessment.AssessmentShortNameDateStartedAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
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
public class AuditLogAssessmentConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> assessmentActivitiesWithNote = List.of(
            AuditLogActivity.ASSESSMENT_VIEW,
            AuditLogActivity.ASSESSMENT_CREATE,
            AuditLogActivity.ASSESSMENT_DOWNLOAD
    );

    @Autowired
    private ClientAssessmentResultService clientAssessmentResultService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (assessmentActivitiesWithNote.contains(activity)) {
                var assessmentAware = clientAssessmentResultService.findById(relatedId, AssessmentShortNameDateStartedAware.class);
                return List.of("Assessment: " + assessmentAware.getAssessmentShortName() + " " + DateTimeUtils.formatDate(assessmentAware.getDateStarted(), zoneId));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.ASSESSMENT;
    }
}
