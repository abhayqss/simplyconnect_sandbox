package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.security.projection.entity.MedicationNameAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.ClientMedicationService;
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
public class AuditLogMedicationConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> medicationActivitiesWithNote = List.of(
            AuditLogActivity.MEDICATION_VIEW
    );

    @Autowired
    private ClientMedicationService clientMedicationService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (medicationActivitiesWithNote.contains(activity)) {
                var medicationAware = clientMedicationService.findById(relatedId, MedicationNameAware.class);
                var medicationName = medicationAware.getMedicationInformationProductNameText();
                return List.of("Medication: " + medicationName);
            }

            if (auditLog.isMobile()) {
                var medicationAware = clientMedicationService.findById(relatedId, MedicationNameAware.class);
                var medicationName = medicationAware.getMedicationInformationProductNameText();
                return List.of("Medication: " + medicationName + "\nMobile app");
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.MEDICATION;
    }
}
