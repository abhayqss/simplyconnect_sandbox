package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.projection.ClientAllergyAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.ClientAllergyService;
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
public class AuditLogAllergyConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> allergyActivitiesWithNote = List.of(
            AuditLogActivity.ALLERGY_VIEW
    );

    @Autowired
    private ClientAllergyService clientAllergyService;


    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (allergyActivitiesWithNote.contains(activity)) {
                var allergyAware = clientAllergyService.findById(relatedId, ClientAllergyAware.class);
                var allergyName = allergyAware.getProductText();
                return List.of("Allergy: " + allergyName);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.ALLERGY;
    }
}
