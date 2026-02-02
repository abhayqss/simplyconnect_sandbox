package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.NoteSecurityAwareEntity;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.NoteDashboardItem;
import com.scnsoft.eldermark.service.basic.AuditableEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface NoteService extends AuditableEntityService<Note>,
        SecurityAwareEntityService<NoteSecurityAwareEntity, Long>,
        ProjectingService<Long> {

    Page<Note> findHistory(Long noteId, Pageable pageRequest);

    Page<Note> findEventNotes(Long eventId, Pageable pageable);

    List<Long> getTakenNoteTypeIds(Long clientId, Long admittanceHistoryId);

    boolean isAdmitDateCanBeTaken(Long clientId, Long subTypeId, Long noteId, Long admittanceHistoryId);

    List<NoteDashboardItem> find(Long clientId, PermissionFilter permissionFilter, Integer limit, Sort sort);

    <P> List<P> find(Specification<Note> specification, Class<P> projectionClass);

    List<IdNamesAware> getAvailableContactNames(Long clientId);

    List<IdNamesAware> getAvailableContactNamesForGroupNote(Long organizationId);
}
