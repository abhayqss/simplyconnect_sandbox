package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.NoteStatisticsFilterDto;
import com.scnsoft.eldermark.beans.projection.EncounterNoteDetailsAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.projection.EncounterNoteCount;
import com.scnsoft.eldermark.service.basic.AuditableEntityService;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public interface EncounterNoteService extends AuditableEntityService<EncounterNote> {

    List<EncounterNoteCount> count(NoteStatisticsFilterDto filter, PermissionFilter permissionFilter);

    <P> List<P> find(Specification<EncounterNote> specification, Class<P> projectClass);

    Map<Long, List<Long>> findGroupNoteClientIds(Specification<EncounterNote> specification);
}
