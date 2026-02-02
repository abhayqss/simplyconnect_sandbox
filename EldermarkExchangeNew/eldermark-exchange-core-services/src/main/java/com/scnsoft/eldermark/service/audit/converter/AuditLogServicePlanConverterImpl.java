package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.entity.serviceplan.DateCreatedAware;
import com.scnsoft.eldermark.service.ServicePlanService;
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
public class AuditLogServicePlanConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> servicePlanActivitiesWithNote = List.of(
            AuditLogActivity.SERVICE_PLAN_VIEW,
            AuditLogActivity.SERVICE_PLAN_UPDATE,
            AuditLogActivity.SERVICE_PLAN_DOWNLOAD,
            AuditLogActivity.SERVICE_PLAN_CREATE
    );

    @Autowired
    private ServicePlanService servicePlanService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (servicePlanActivitiesWithNote.contains(activity)) {
                return List.of("SP created " + DateTimeUtils.formatDate(servicePlanService.findById(relatedId, DateCreatedAware.class).getDateCreated(), zoneId));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SERVICE_PLAN;
    }
}
