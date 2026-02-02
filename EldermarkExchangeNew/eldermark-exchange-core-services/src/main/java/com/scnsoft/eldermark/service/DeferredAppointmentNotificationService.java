package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.event.DeferredAppointmentNotification;

import java.util.List;

public interface DeferredAppointmentNotificationService {

    void schedule(List<DeferredAppointmentNotification> notifications);

    void schedule(DeferredAppointmentNotification notification);

}
