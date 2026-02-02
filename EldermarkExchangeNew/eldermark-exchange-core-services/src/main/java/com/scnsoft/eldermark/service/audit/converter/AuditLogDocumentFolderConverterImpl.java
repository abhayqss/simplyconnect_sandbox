package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class AuditLogDocumentFolderConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> folderActivitiesWithNote = List.of(
            AuditLogActivity.CREATE_FOLDER,
            AuditLogActivity.EDIT_FOLDER,
            AuditLogActivity.DELETE_FOLDER
    );

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedAdditionalFields)) {
            var folderName = relatedAdditionalFields.get(0);

            if (folderActivitiesWithNote.contains(activity)) {
                if (AuditLogActivity.EDIT_FOLDER.equals(activity) && !CollectionUtils.isEmpty(relatedAdditionalFields)) {
                    var oldFolderName = relatedAdditionalFields.get(1);
                    return !oldFolderName.equals(folderName)
                            ? List.of(oldFolderName + " -> " + folderName)
                            : Collections.emptyList();
                }
                return List.of(folderName);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.DOCUMENT_FOLDER;
    }
}
