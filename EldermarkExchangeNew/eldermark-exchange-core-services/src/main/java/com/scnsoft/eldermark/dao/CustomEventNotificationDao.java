package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.EventNotification;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Optional;

public interface CustomEventNotificationDao {

    Optional<Instant> findFirstSentDatetime(Specification<EventNotification> specification);
}