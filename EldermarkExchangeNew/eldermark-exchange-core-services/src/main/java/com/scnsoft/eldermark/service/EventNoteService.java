package com.scnsoft.eldermark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.EventNote;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Optional;

public interface EventNoteService {

    Page<EventNote> find(EventNoteFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    Long findEventPageNumber(Long eventId, EventNoteFilter filter, PermissionFilter permissionFilter, int pageSize, Sort sort);

    Long findNotePageNumber(Long noteId, EventNoteFilter filter, PermissionFilter permissionFilter, int pageSize, Sort sort);

    Long count(EventNoteFilter filter, PermissionFilter permissionFilter);

    Optional<Instant> findOldestDate(EventNoteFilter filter, PermissionFilter permissionFilter);

    Optional<Instant> findNewestDate(EventNoteFilter filter, PermissionFilter permissionFilter);
}
