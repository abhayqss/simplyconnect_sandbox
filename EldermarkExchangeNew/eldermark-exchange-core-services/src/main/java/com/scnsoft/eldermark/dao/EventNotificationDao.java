package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.EventNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventNotificationDao extends JpaRepository<EventNotification, Long>, CustomEventNotificationDao {

    <T> List<T> findAllByEvent_IdAndEmployee_IdAndCareTeamRole_Id(Long eventId, Long employeeId, Long careTeamRoleId, Class<T> type);
}
