package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.dao.AffiliatedRelationshipNotificationDao;
import com.scnsoft.eldermark.dto.notification.affiliated.AffiliatedRelationshipNotificationMailDto;
import com.scnsoft.eldermark.entity.AffiliatedRelationshipNotification;
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
public class AffiliatedRelationshipNotificationSenderImpl implements AffiliatedRelationshipNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(AffiliatedRelationshipNotificationSenderImpl.class);

    @Autowired
    private AffiliatedRelationshipNotificationDao affiliatedRelationshipNotificationDao;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private Converter<AffiliatedRelationshipNotification, AffiliatedRelationshipNotificationMailDto> affiliateRelationshipNotificationMailDtoConverter;

    @Async
    @Transactional
    @Override
    public void send(Long notificationId) {
        var notification = affiliatedRelationshipNotificationDao.getOne(notificationId);
        try {
            boolean result = exchangeMailService.sendAffiliatedRelationshipNotificationAndWait(affiliateRelationshipNotificationMailDtoConverter.convert(notification));
            if (result) {
                logger.info("Affiliated relationship notification [{}] was sent", notificationId);
                notification.setSentDatetime(Instant.now());
                affiliatedRelationshipNotificationDao.save(notification);
            } else {
                logger.info("Affiliated relationship notification [{}] wasn't sent", notificationId);
            }
        } catch (RuntimeException ex) {
            logger.warn("Couldn't send affiliated relationship notification [{}]", notificationId, ex);
        }
    }
}
