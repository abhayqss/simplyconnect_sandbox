package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAppointmentDao extends AppJpaRepository<ClientAppointment, Long> {
}
