package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.EventNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventNotificationJpaDao  extends JpaRepository<EventNotification, Long> {
    List<EventNotification> findAllByEvent_Id(Long eventId);
}
