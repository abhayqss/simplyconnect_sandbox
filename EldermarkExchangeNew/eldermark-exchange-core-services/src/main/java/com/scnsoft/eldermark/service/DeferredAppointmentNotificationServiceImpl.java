package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.DeferredAppointmentNotificationDao;
import com.scnsoft.eldermark.entity.event.DeferredAppointmentNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DeferredAppointmentNotificationServiceImpl implements DeferredAppointmentNotificationService {

    @Value("${appointment.notification.deferred.scheduler.refreshTime}")
    private Long refreshPeriod;

    @Autowired
    private ThreadPoolTaskScheduler scheduledExecutorService;

    @Autowired
    private DeferredAppointmentNotificationDao notificationDao;

    @Autowired
    private DeferredAppointmentNotificationSender notificationSender;

    @Override
    @Transactional
    public void schedule(List<DeferredAppointmentNotification> notifications) {
        notificationDao.saveAll(notifications);
        notifications.forEach(this::scheduleIfDispatchTimeIsInRefreshPeriod);
    }

    @Override
    @Transactional
    public void schedule(DeferredAppointmentNotification notification) {
        notificationDao.save(notification);
        scheduleIfDispatchTimeIsInRefreshPeriod(notification);
    }

    private void scheduleIfDispatchTimeIsInRefreshPeriod(DeferredAppointmentNotification notification) {
        scheduledExecutorService.schedule(() -> notificationSender.send(notification.getId()), notification.getDispatchDatetime());
    }

    @Scheduled(fixedRateString = "${appointment.notification.deferred.scheduler.refreshTime}")
    public void refreshScheduledNotifications() {
        var notifications = notificationDao.findAllByDispatchDatetimeBefore(Instant.now().plus(refreshPeriod, ChronoUnit.MILLIS));
        notifications.forEach(this::scheduleIfDispatchTimeIsInRefreshPeriod);
    }
}
