package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.DemoRequestSubmittedNotificationDao;
import com.scnsoft.eldermark.dto.notification.DemoRequestSubmittedNotificationDto;
import com.scnsoft.eldermark.entity.DemoRequest;
import com.scnsoft.eldermark.entity.DemoRequestSubmittedNotification;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemoRequestNotificationServiceImpl implements DemoRequestNotificationService {

    private final Logger logger = LoggerFactory.getLogger(DemoRequestNotificationServiceImpl.class);

    @Autowired
    private SupportConfigurationService supportConfigurationService;

    @Autowired
    private DemoRequestSubmittedNotificationDao demoRequestSubmittedNotificationDao;

    @Autowired
    private ExchangeMailService mailService;

    @Override
    @Transactional
    public void sendDemoRequestSubmittedNotifications(DemoRequest demoRequest) {

        var notifications = createNotifications(demoRequest);

        notifications.forEach(notification -> {
            var dto = createEmailNotificationDto(notification);
            var sendResultFuture = mailService.sendDemoRequestedNotification(dto);

            sendResultFuture.thenAccept(sentResult -> {
                if (sentResult) {
                    markNotificationAsSent(notification);
                }
            });
        });
    }

    private DemoRequestSubmittedNotificationDto createEmailNotificationDto(DemoRequestSubmittedNotification notification) {

        var dto = new DemoRequestSubmittedNotificationDto();

        var author = notification.getDemoRequest().getAuthor();
        dto.setAuthorName(author.getFullName());
        dto.setAuthorOrganizationName(author.getOrganization().getName());
        dto.setDemoTitle(notification.getDemoRequest().getDemoTitle());
        PersonTelecomUtils.findValue(author.getPerson(), PersonTelecomCode.MC)
                .ifPresent(dto::setAuthorPhoneNumber);
        dto.setDate(notification.getCreatedDate());
        dto.setReceiverEmail(notification.getReceiverEmail());

        return dto;
    }

    private List<DemoRequestSubmittedNotification> createNotifications(DemoRequest demoRequest) {

        var notifications = supportConfigurationService.getSupportTicketReceiverEmails().stream()
                .map(receiverEmail -> {
                    var notification = new DemoRequestSubmittedNotification();
                    notification.setDemoRequest(demoRequest);
                    notification.setReceiverEmail(receiverEmail);
                    notification.setCreatedDate(Instant.now());
                    return notification;
                })
                .collect(Collectors.toList());

        demoRequestSubmittedNotificationDao.saveAll(notifications);

        return notifications;
    }

    private void markNotificationAsSent(DemoRequestSubmittedNotification notification) {
        try {
            demoRequestSubmittedNotificationDao.updateSentDateById(notification.getId(), Instant.now());
        } catch (Exception e) {
            logger.error("Error during updating demo request notification", e);
        }
    }
}
