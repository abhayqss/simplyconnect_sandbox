package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;

public interface ClientAppointmentNotificationService {

    void createStaffUpcomingAppointmentNotification(ClientAppointment appointment);

    void createClientAppointmentCanceledNotification(ClientAppointment appointment);

    void createClientAppointmentUpdatedNotification(ClientAppointment appointment);

    void createClientAppointmentReminderNotifications(ClientAppointment appointment);

    void createClientAppointmentCompletedNotification(ClientAppointment appointment);
}
