package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.event.DeferredAppointmentNotification;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DeferredAppointmentNotificationDao extends AppJpaRepository<DeferredAppointmentNotification, Long> {

    List<DeferredAppointmentNotification> findAllByDispatchDatetimeBefore(Instant dispatchDatetimeBefore);

}
