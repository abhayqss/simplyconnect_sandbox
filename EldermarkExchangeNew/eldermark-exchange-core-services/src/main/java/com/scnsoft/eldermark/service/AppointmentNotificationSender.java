package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.event.AppointmentNotificationType;
import com.scnsoft.eldermark.entity.event.ClientAppointmentNotification;

import java.util.List;

public interface AppointmentNotificationSender {

    void sendClientNotifications(List<ClientAppointmentNotification> notifications);

    void sendClientNotification(ClientAppointmentNotification notification);

    void sendStaffNotification(ClientAppointment clientAppointment, AppointmentNotificationType type);
}
