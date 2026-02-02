package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.EventNoteDao;
import com.scnsoft.eldermark.dao.specification.EventNoteSpecificationGenerator;
import com.scnsoft.eldermark.entity.EventNote;
import com.scnsoft.eldermark.entity.EventNote_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class EventNoteServiceImpl implements EventNoteService {

    @Autowired
    private EventNoteDao eventNoteDao;

    @Autowired
    private EventNoteSpecificationGenerator eventNoteSpecificationGenerator;

    @Override
    @Transactional(readOnly = true)
    public Page<EventNote> find(EventNoteFilter filter, PermissionFilter permissionFilter, Pageable pageable) {
        var byFilterAndHasAccess = eventNoteSpecificationGenerator.byFilterAndHasAccess(filter, permissionFilter);
        return eventNoteDao.findAll(byFilterAndHasAccess, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Long findEventPageNumber(Long eventId, EventNoteFilter filter, PermissionFilter permissionFilter, int pageSize, Sort sort) {
        var byFilterAndHasAccess = eventNoteSpecificationGenerator.byFilterAndHasAccess(filter, permissionFilter);

        return eventNoteDao.findPageNumber(eventNoteRoot -> eventNoteRoot.get(EventNote_.EVENT_ID),
                eventId, byFilterAndHasAccess, EventNote.class, pageSize, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public Long findNotePageNumber(Long noteId, EventNoteFilter filter, PermissionFilter permissionFilter, int pageSize, Sort sort) {
        var byFilterAndHasAccess = eventNoteSpecificationGenerator.byFilterAndHasAccess(filter, permissionFilter);

        return eventNoteDao.findPageNumber(eventNoteRoot -> eventNoteRoot.get(EventNote_.NOTE_ID),
                noteId, byFilterAndHasAccess, EventNote.class, pageSize, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(EventNoteFilter filter, PermissionFilter permissionFilter) {
        var byFilterAndHasAccess = eventNoteSpecificationGenerator.byFilterAndHasAccess(filter, permissionFilter);
        return eventNoteDao.count(byFilterAndHasAccess);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instant> findOldestDate(EventNoteFilter filter, PermissionFilter permissionFilter) {
        var byFilterAndHasAccess = eventNoteSpecificationGenerator.byFilterAndHasAccess(filter, permissionFilter);
        return eventNoteDao.findMinDate(byFilterAndHasAccess);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instant> findNewestDate(EventNoteFilter filter, PermissionFilter permissionFilter) {
        var byFilterAndHasAccess = eventNoteSpecificationGenerator.byFilterAndHasAccess(filter, permissionFilter);
        return eventNoteDao.findMaxDate(byFilterAndHasAccess);
    }
}
