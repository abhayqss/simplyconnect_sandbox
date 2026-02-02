package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.event.AppointmentNotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ClientAppointmentNotificationServiceImpl implements ClientAppointmentNotificationService {

    @Autowired
    private AppointmentNotificationSender appointmentNotificationSender;

    @Autowired
    private DeferredAppointmentNotificationService deferredAppointmentNotificationService;

    @Autowired
    private ClientAppointmentNotificationBuilder notificationBuilder;

    @Override
    public void createStaffUpcomingAppointmentNotification(ClientAppointment appointment) {
        var dispatchTime = appointment.getDateFrom().minus(1, ChronoUnit.DAYS);
        if (dispatchTime.isAfter(Instant.now())) {
            deferredAppointmentNotificationService.schedule(notificationBuilder.createDeferredStaffUpcomingAppointmentNotification(appointment, dispatchTime));
        }
    }

    @Override
    public void createClientAppointmentCanceledNotification(ClientAppointment appointment) {
        appointmentNotificationSender.sendClientNotifications(notificationBuilder.createClientNotifications(appointment, AppointmentNotificationType.CLIENT_CANCELED_EVENT));
    }

    @Override
    public void createClientAppointmentUpdatedNotification(ClientAppointment appointment) {
        appointmentNotificationSender.sendClientNotifications(notificationBuilder.createClientNotifications(appointment, AppointmentNotificationType.CLIENT_UPDATED_EVENT));
    }

    @Override
    public void createClientAppointmentReminderNotifications(ClientAppointment appointment) {
        deferredAppointmentNotificationService.schedule(notificationBuilder.createDeferredReminderNotifications(appointment));
    }

    @Override
    public void createClientAppointmentCompletedNotification(ClientAppointment appointment) {
        appointmentNotificationSender.sendClientNotifications(notificationBuilder.createClientNotifications(appointment, AppointmentNotificationType.CLIENT_COMPLETED_EVENT));
    }
}
