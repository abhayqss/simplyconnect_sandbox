package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.LabResearchEmployeeNotificationDao;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.lab.LabResearchNotificationType;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.lab.LabResearchResultsEmployeeNotification;
import com.scnsoft.eldermark.service.notification.sender.LabResearchEmployeeNotificationSender;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabResearchNotificationServiceImpl implements LabResearchNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(LabResearchNotificationServiceImpl.class);

    private static final String NOTIFICATIONS_ENABLED_PROPERTY = "labs.resultsNotification.enabled";
    private static final String NOTIFICATIONS_CRON_PROPERTY = "labs.resultsNotification.period";
    private static final String RUN_NOTIFICATIONS_CRON_IF_ENABLED = "${" + NOTIFICATIONS_ENABLED_PROPERTY + "?" +
            NOTIFICATIONS_CRON_PROPERTY + ":" + Scheduled.CRON_DISABLED + "}";


    @Value("${" + NOTIFICATIONS_ENABLED_PROPERTY + "}")
    private boolean labResultsNotificationsEnabled;

    @Autowired
    private LabResearchOrderService labResearchOrderService;

    @Autowired
    private LabResearchEmployeeNotificationDao labResearchEmployeeNotificationDao;

    @Autowired
    private LabResearchEmployeeNotificationSender labResearchEmployeeNotificationSender;

    @PostConstruct
    void logNotificationsEnabled() {
        if (!labResultsNotificationsEnabled) {
            logger.info("Scheduled sending of lab results received notifications is disabled");
        }
    }

    @Scheduled(cron = RUN_NOTIFICATIONS_CRON_IF_ENABLED)
    @Transactional
    public void sendNotifications() {
        if (labResultsNotificationsEnabled) {
            logger.info("Scheduled sending of lab results received notifications has been started");
            sendResultReceivedNotification(labResearchEmployeeNotificationDao.findBySentDatetimeIsNull());
        } else {
            //scheduled should not have been started at all in this case, but just to be sure leaving check
            logger.info("Scheduled sending of lab results received notifications is disabled");
        }
    }

    @Transactional
    public void prepareResultReceivedNotification(LabResearchOrder labResearchOrder) {
        prepareResultReceivedNotifications(labResearchOrder);
    }

    private List<LabResearchResultsEmployeeNotification> prepareResultReceivedNotifications(LabResearchOrder labResearchOrder) {
        var labOrderReviewers = labResearchOrderService.findReviewers(labResearchOrder);
        var notifications = labOrderReviewers.stream()
                .filter(employee -> !labResearchEmployeeNotificationDao.existsByEmployeeAndSentDatetimeIsNull(employee))
                .map(c -> {
                    var notification = new LabResearchResultsEmployeeNotification();
                    notification.setType(LabResearchNotificationType.RESULT_RECEIVED);
                    notification.setDestination(PersonTelecomUtils.findValue(c.getPerson(), PersonTelecomCode.EMAIL).orElse(null));
                    notification.setEmployee(c);
                    notification.setCreatedDatetime(Instant.now());
                    return notification;
                }).collect(Collectors.toList());
        return labResearchEmployeeNotificationDao.saveAll(notifications);
    }

    private void sendResultReceivedNotification(List<LabResearchResultsEmployeeNotification> notifications) {
        notifications.stream()
                .filter(n -> Strings.isNotEmpty(n.getDestination()))
                .map(LabResearchResultsEmployeeNotification::getId)
                .forEach(this::send);
    }

    private void send(Long notificationId) {
        labResearchEmployeeNotificationSender.sendResultReceivedNotification(notificationId);
    }
}
