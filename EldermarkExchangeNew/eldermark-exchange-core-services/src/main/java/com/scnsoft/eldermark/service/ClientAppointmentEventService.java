package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;

public interface ClientAppointmentEventService {

    void createAppointmentCreatedEvent(ClientAppointment newAppointment);

    void createAppointmentUpdatedEvent(ClientAppointment previousAppointment, ClientAppointment newAppointment);

    void createAppointmentCanceledEvent(ClientAppointment canceledAppointment);

    void createAppointmentCompletedEvent(ClientAppointment updatedAppointment);
}
