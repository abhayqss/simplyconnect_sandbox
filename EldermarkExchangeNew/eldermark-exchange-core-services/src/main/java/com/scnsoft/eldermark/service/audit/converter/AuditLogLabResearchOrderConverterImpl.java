package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.entity.lab.CreatedDateAware;
import com.scnsoft.eldermark.service.LabResearchOrderService;
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
public class AuditLogLabResearchOrderConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> labResearchOrderActivitiesWithNote = List.of(
            AuditLogActivity.LAB_ORDER_VIEW,
            AuditLogActivity.LAB_ORDER_PLACE,
            AuditLogActivity.LAB_ORDER_REVIEW
    );

    @Autowired
    private LabResearchOrderService labResearchOrderService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (labResearchOrderActivitiesWithNote.contains(activity)) {
                return List.of("Order date " + DateTimeUtils.formatDate(labResearchOrderService.findById(relatedId, CreatedDateAware.class).getCreatedDate(), zoneId));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.LAB_RESEARCH_ORDER;
    }
}
