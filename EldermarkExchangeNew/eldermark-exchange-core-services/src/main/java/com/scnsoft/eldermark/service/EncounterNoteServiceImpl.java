package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.NoteStatisticsFilterDto;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.EncounterNoteDao;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.projection.EncounterNoteCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EncounterNoteServiceImpl extends BaseNoteService<EncounterNote> implements EncounterNoteService {

    @Autowired
    private EncounterNoteDao encounterNoteDao;

    @Override
    public List<EncounterNoteCount> count(NoteStatisticsFilterDto filter, PermissionFilter permissionFilter) {
        //todo implement with proper security like in ClientEventStatisticsServiceImpl
        return Collections.emptyList();
//        return encounterNoteDao.count(filter.getClientId(), filter.getFromDate(),
//                filter.getToDate());
    }

    @Override
    public <P> List<P> find(Specification<EncounterNote> specification, Class<P> projectClass) {
        return encounterNoteDao.findAll(specification, projectClass);
    }

    @Override
    public Map<Long, List<Long>> findGroupNoteClientIds(Specification<EncounterNote> specification) {
        return encounterNoteDao.findGroupNoteClientIds(specification);
    }

    @Override
    public EncounterNote save(EncounterNote entity) {
        return encounterNoteDao.saveAndFlush(entity);
    }

    @Override
    public EncounterNote findById(Long id) {
        return encounterNoteDao.findById(id).orElse(null);
    }
}
