package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeDao extends JpaRepository<EventType, Long> {
    EventType getByCode(String code);
}
