package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.note.EncounterNote;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomEncounterNoteDao {
    Map<Long, Set<Long>> findClientIdsByEncounterNoteIdMap(List<Long> ids);
    Map<Long, List<Long>> findGroupNoteClientIds(Specification<EncounterNote> specification);
}
