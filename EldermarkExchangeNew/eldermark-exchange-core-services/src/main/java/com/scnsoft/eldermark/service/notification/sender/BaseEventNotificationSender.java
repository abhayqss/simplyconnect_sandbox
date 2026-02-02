package com.scnsoft.eldermark.service.notification.sender;


import com.scnsoft.eldermark.dao.EventNotificationDao;
import com.scnsoft.eldermark.entity.EventNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


public abstract class BaseEventNotificationSender implements EventNotificationSender {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventNotificationDao eventNotificationDao;

    @Override
    @Async
    @Transactional
    public void send(Long eventNotificationId) {
        logger.info("Current thread {}", Thread.currentThread().getId());
        logger.info("Sending notification [{}]", eventNotificationId);
        try {
            var notification = eventNotificationDao.getOne(eventNotificationId);
            if (send(notification)) {
                logger.info("Event notification [{}] was sent", eventNotificationId);
                notification.setSentDatetime(Instant.now());
                eventNotificationDao.save(notification);
            } else {
                logger.info("Event notification [{}] wasn't sent", eventNotificationId);
            }
        } catch (RuntimeException ex) {
            logger.warn("Couldn't send event notification [{}]", eventNotificationId, ex);
        }
    }

    protected abstract boolean send(EventNotification eventNotification);
}
