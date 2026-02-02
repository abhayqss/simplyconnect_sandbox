package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.dao.IncidentReportSubmitNotificationDao;
import com.scnsoft.eldermark.dto.notification.lab.IncidentReportSubmitNotificationMailDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportSubmitNotification;
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
public class IncidentReportSubmitNotificationSenderImpl implements IncidentReportSubmitNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(IncidentReportSubmitNotificationSenderImpl.class);

    @Autowired
    private IncidentReportSubmitNotificationDao incidentReportEmployeeNotificationDao;

    @Autowired
    private Converter<IncidentReportSubmitNotification, IncidentReportSubmitNotificationMailDto> incidentReportSubmitNotificationMailDtoConverter;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Async
    @Transactional
    @Override
    public void send(Long id) {
        var notification = incidentReportEmployeeNotificationDao.getOne(id);
        try {
            boolean result = exchangeMailService.sendIncidentReportSubmitNotificationAndWait(incidentReportSubmitNotificationMailDtoConverter.convert(notification));
            if (result) {
                logger.info("Notification [{}] about submitting the incident report was sent", notification.getId());
                notification.setSentDatetime(Instant.now());
                incidentReportEmployeeNotificationDao.save(notification);
            } else {
                logger.info("Notification [{}] about submitting the incident report wasn't sent", notification.getId());
            }
        } catch (RuntimeException ex) {
            logger.warn("Couldn't send notification [{}] about submitting the incident report", notification.getId(), ex);
        }
    }
}
