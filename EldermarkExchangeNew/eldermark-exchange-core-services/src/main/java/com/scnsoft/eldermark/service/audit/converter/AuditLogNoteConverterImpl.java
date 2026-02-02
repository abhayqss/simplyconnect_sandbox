package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.entity.note.NoteDateTypeAware;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.entity.note.NoteTypeAware;
import com.scnsoft.eldermark.service.NoteService;
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
public class AuditLogNoteConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> noteActivitiesWithNote = List.of(
            AuditLogActivity.NOTE_VIEW,
            AuditLogActivity.NOTE_CREATE,
            AuditLogActivity.NOTE_GROUP_CREATE
    );

    private static final List<AuditLogActivity> noteActivitiesWithTypeNote = List.of(
            AuditLogActivity.NOTE_EDIT,
            AuditLogActivity.NOTE_GROUP_EDIT
    );

    @Autowired
    private NoteService noteService;

    @Override
    public AuditLogActivity convertToAuditLogActivity(AuditLog auditLog, List<Long> relatedIds) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            if (AuditLogAction.NOTE_CREATE == auditLog.getAction()) {
                var noteType = noteService.findById(relatedIds.get(0), NoteTypeAware.class).getType();
                if (NoteType.GROUP_NOTE == noteType) {
                    return AuditLogActivity.NOTE_GROUP_CREATE;
                }
                return AuditLogActivity.NOTE_CREATE;
            }
            if (AuditLogAction.NOTE_EDIT == auditLog.getAction()) {
                var noteType = noteService.findById(relatedIds.get(0), NoteTypeAware.class).getType();
                if (NoteType.GROUP_NOTE == noteType) {
                    return AuditLogActivity.NOTE_GROUP_EDIT;
                }
                return AuditLogActivity.NOTE_EDIT;
            }
        }

        return AuditLogActivity.getByAuditLogAction(auditLog.getAction());
    }

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (noteActivitiesWithNote.contains(activity)) {
                var noteAware = noteService.findById(relatedId, NoteDateTypeAware.class);
                return List.of("Note date & time " + DateTimeUtils.formatDateTime(noteAware.getNoteDate(), zoneId), noteAware.getType().getDisplayName());
            }
            if (noteActivitiesWithTypeNote.contains(activity)) {
                return List.of(noteService.findById(relatedId, NoteTypeAware.class).getType().getDisplayName());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.NOTE;
    }
}
