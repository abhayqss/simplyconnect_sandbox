package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.event.ClientAppointmentNotification;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAppointmentNotificationDao extends AppJpaRepository<ClientAppointmentNotification, Long> {
}
