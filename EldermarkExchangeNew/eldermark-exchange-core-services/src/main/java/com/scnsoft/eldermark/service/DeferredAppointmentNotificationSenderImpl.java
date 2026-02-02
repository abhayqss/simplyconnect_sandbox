package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.DeferredAppointmentNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeferredAppointmentNotificationSenderImpl implements DeferredAppointmentNotificationSender {

    @Autowired
    private DeferredAppointmentNotificationDao deferredAppointmentNotificationDao;

    @Autowired
    private ClientAppointmentNotificationBuilder clientAppointmentNotificationBuilder;

    @Autowired
    private AppointmentNotificationSender appointmentNotificationSender;

    @Override
    @Transactional
    public void send(Long id) {
        deferredAppointmentNotificationDao.findById(id)
                .ifPresent(deferredNotification -> {
                    var appointment = deferredNotification.getAppointment();
                    if (!appointment.getArchived() && appointment.getClient().getOrganization().getIsAppointmentsEnabled()) {
                        if (deferredNotification.getType().isStaffNotification()) {
                            appointmentNotificationSender.sendStaffNotification(appointment, deferredNotification.getType());
                        } else if (deferredNotification.getType().isClientNotification()) {
                            var notifications = clientAppointmentNotificationBuilder.createClientNotifications(appointment, deferredNotification.getType());
                            appointmentNotificationSender.sendClientNotifications(notifications);
                        }
                    }
                    deferredAppointmentNotificationDao.delete(deferredNotification);
                });
    }
}
