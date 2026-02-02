package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.EventNote;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Optional;

public interface CustomEventNoteDao {

    Optional<Instant> findMaxDate(Specification<EventNote> specification);

    Optional<Instant> findMinDate(Specification<EventNote> specification);
}
