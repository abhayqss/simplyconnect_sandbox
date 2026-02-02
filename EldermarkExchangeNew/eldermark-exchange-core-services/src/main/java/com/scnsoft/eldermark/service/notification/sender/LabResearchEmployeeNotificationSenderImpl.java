package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.dao.LabResearchEmployeeNotificationDao;
import com.scnsoft.eldermark.dto.notification.lab.LabResearchTestResultReceivedNotificationMailDto;
import com.scnsoft.eldermark.entity.lab.LabResearchResultsEmployeeNotification;
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
public class LabResearchEmployeeNotificationSenderImpl implements LabResearchEmployeeNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(LabResearchEmployeeNotificationSenderImpl.class);

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private LabResearchEmployeeNotificationDao labResearchOrganizationNotificationDao;

    @Autowired
    private Converter<LabResearchResultsEmployeeNotification, LabResearchTestResultReceivedNotificationMailDto> receivedNotificationMailDtoConverter;

    @Async
    @Transactional
    @Override
    public void sendResultReceivedNotification(Long id) {
        var notification = labResearchOrganizationNotificationDao.getOne(id);
        try {
            boolean result = exchangeMailService.sendLabResearchTestResultReceivedNotificationAndWait(receivedNotificationMailDtoConverter.convert(notification));
            if (result) {
                logger.info("Notification [{}] about receiving lab result was sent", notification.getId());
                notification.setSentDatetime(Instant.now());
                labResearchOrganizationNotificationDao.save(notification);
            } else {
                logger.info("Notification [{}] about receiving lab result wasn't sent", notification.getId());
            }
        } catch (RuntimeException ex) {
            logger.warn("Couldn't send notification [{}] about receiving lab result", notification.getId(), ex);
        }
    }
}
