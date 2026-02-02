package com.scnsoft.eldermark.event.xml.dao;

import com.scnsoft.eldermark.event.xml.entity.EventsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsLogDao extends JpaRepository<EventsLog, Long> {
}
