package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.GroupedEventNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupedEventNotificationDao extends JpaRepository<GroupedEventNotification, Long> {
    Page<GroupedEventNotification> findAllByEvent_Id(Long eventId, Pageable pageable);
    Long countAllByEvent_Id(Long eventId);
}
