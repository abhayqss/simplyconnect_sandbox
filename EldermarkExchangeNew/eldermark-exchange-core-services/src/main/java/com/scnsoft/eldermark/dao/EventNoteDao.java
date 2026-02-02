package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.EventNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventNoteDao extends JpaRepository<EventNote, Long>, JpaSpecificationExecutor<EventNote>, PageNumberDao<EventNote, Long>, CustomEventNoteDao {
}
