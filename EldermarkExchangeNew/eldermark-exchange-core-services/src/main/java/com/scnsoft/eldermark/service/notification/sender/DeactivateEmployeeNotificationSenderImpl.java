package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.dao.DeactivateEmployeeNotificationDao;
import com.scnsoft.eldermark.dto.notification.deactivate.DeactivateEmployeeNotificationMailDto;
import com.scnsoft.eldermark.entity.DeactivateEmployeeNotification;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DeactivateEmployeeNotificationSenderImpl implements DeactivateEmployeeNotificationSender {

    private final Logger logger = LoggerFactory.getLogger(DeactivateEmployeeNotificationSenderImpl.class);

    @Autowired
    private DeactivateEmployeeNotificationDao deactivateEmployeeNotificationDao;

    @Autowired
    private Converter<DeactivateEmployeeNotification, DeactivateEmployeeNotificationMailDto> deactivateEmployeeNotificationMailDtoConverter;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Async
    @Transactional
    @Override
    public void send(Long id) {
        var notification = deactivateEmployeeNotificationDao.getOne(id);
        try {
            boolean result = exchangeMailService.sendDeactivateEmployeeNotificationAndWait(deactivateEmployeeNotificationMailDtoConverter.convert(notification));
            if (result) {
                logger.info("Notification [{}] about deactivate employee was sent", notification.getId());
                notification.setSentDatetime(Instant.now());
                deactivateEmployeeNotificationDao.save(notification);
            } else {
                logger.info("Notification [{}] about deactivate employee wasn't sent", notification.getId());
            }
        } catch (RuntimeException ex) {
            logger.warn("Couldn't send notification [{}] about deactivate employee", notification.getId(), ex);
        }
    }
}
