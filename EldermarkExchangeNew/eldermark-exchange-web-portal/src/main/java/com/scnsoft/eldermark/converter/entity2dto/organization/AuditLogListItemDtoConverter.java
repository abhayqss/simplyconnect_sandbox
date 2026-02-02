package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.projection.NamesAware;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.AuditLogListItemDto;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogRelation;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.entity.note.NoteTypeEventIdAware;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.NoteService;
import com.scnsoft.eldermark.service.audit.AuditLogConverterService;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class AuditLogListItemDtoConverter implements ListAndItemConverter<Pair<AuditLog, ZoneId>, AuditLogListItemDto> {


    @Autowired
    private ClientService clientService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private AuditLogConverterService auditLogConverterService;

    @Override
    public AuditLogListItemDto convert(Pair<AuditLog, ZoneId> source) {
        var target = new AuditLogListItemDto();
        var auditLog = source.getFirst();
        target.setRelatedIds(Optional.ofNullable(auditLog.getAuditLogRelation()).map(AuditLogRelation::getRelatedIds).orElse(null));
        target.setRelatedAdditionalFields(Optional.ofNullable(auditLog.getAuditLogRelation()).map(AuditLogRelation::getAdditionalFields).orElse(null));
        var converterType = Optional.ofNullable(auditLog.getAuditLogRelation()).map(AuditLogRelation::getConverterType).orElse(null);

        var activity = auditLogConverterService.convertToAuditLogActivity(converterType, auditLog, target.getRelatedIds());
        if (activity == null) {
            return null;
        }
        target.setClients(clientService.findAllById(auditLog.getClientIds(), IdNamesAware.class).stream()
                .map(aware -> new IdentifiedNamedEntityDto(aware.getId(), aware.getFullName()))
                .collect(Collectors.toList()));
        target.setEmployeeName(employeeService.findById(auditLog.getEmployeeId(), NamesAware.class).getFullName());
        target.setDate(auditLog.getDate().toEpochMilli());
        target.setActivityName(activity.name());
        target.setActivityTitle(activity.getDisplayName());
        target.setNotes(auditLogConverterService.convertNotes(converterType, activity, auditLog, target.getRelatedIds(),target.getRelatedAdditionalFields(), source.getSecond()));
        target.setId(auditLog.getId());
        if (activity == AuditLogActivity.NOTE_EDIT) {
            var aware = noteService.findById(target.getRelatedIds().get(0), NoteTypeEventIdAware.class);
            if (NoteType.EVENT_NOTE == aware.getType()) {
                target.setEventId(aware.getEventId());
            }
        }
        return target;
    }
}
