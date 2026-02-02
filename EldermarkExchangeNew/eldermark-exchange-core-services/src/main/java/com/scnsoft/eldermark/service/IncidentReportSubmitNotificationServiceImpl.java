package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.IncidentReportSubmitNotificationDao;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportSubmitNotification;
import com.scnsoft.eldermark.service.notification.sender.IncidentReportSubmitNotificationSender;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IncidentReportSubmitNotificationServiceImpl implements IncidentReportSubmitNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(IncidentReportSubmitNotificationServiceImpl.class);

    @Value("${incident.report.notification.enabled}")
    private boolean incidentReportNotificationsEnabled;

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private IncidentReportSubmitNotificationDao incidentReportEmployeeNotificationDao;

    @Autowired
    private IncidentReportSubmitNotificationSender incidentReportEmployeeNotificationSender;

    @Override
    public void sendNotifications(IncidentReport incidentReport) {
        var notifications = prepareSubmitNotifications(incidentReport);
        if (incidentReportNotificationsEnabled) {
            logger.info("Sending of incident report notifications has been started");
            sendNotifications(notifications);
        } else {
            logger.info("Sending of incident report notifications is disabled");
        }
    }

    private List<IncidentReportSubmitNotification> prepareSubmitNotifications(IncidentReport incidentReport) {
        var incidentReportReviewers = incidentReportService.findReviewers(incidentReport);
        var notifications = incidentReportReviewers.stream()
                .map(employee -> {
                    var notification = new IncidentReportSubmitNotification();
                    notification.setIncidentReport(incidentReport);
                    notification.setCreatedDatetime(Instant.now());
                    notification.setEmployee(employee);
                    notification.setDestination(PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.EMAIL).orElse(null));
                    return notification;
                }).collect(Collectors.toList());
        return incidentReportEmployeeNotificationDao.saveAll(notifications);
    }

    private void sendNotifications(List<IncidentReportSubmitNotification> notifications) {
        notifications.stream()
                .filter(n -> Strings.isNotEmpty(n.getDestination()))
                .map(IncidentReportSubmitNotification::getId)
                .forEach(this::send);
    }

    private void send(Long notificationId) {
        incidentReportEmployeeNotificationSender.send(notificationId);
    }
}
