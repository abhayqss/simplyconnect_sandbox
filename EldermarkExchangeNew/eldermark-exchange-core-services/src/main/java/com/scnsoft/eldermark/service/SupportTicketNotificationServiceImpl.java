package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.SupportTicketSubmittedNotificationDao;
import com.scnsoft.eldermark.dto.notification.SupportTicketSubmittedNotificationDto;
import com.scnsoft.eldermark.entity.SupportTicketAttachment;
import com.scnsoft.eldermark.entity.SupportTicketSubmittedNotification;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.service.storage.SupportTicketAttachmentFileStorage;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class SupportTicketNotificationServiceImpl implements SupportTicketNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketNotificationServiceImpl.class);

    @Autowired
    private SupportTicketAttachmentFileStorage supportTicketAttachmentFileStorage;

    @Autowired
    private SupportTicketSubmittedNotificationDao ticketSubmittedNotificationDao;

    @Autowired
    private ExchangeMailService mailService;

    @Override
    @Transactional
    public void sendNotifications(List<SupportTicketSubmittedNotification> notifications) {

        var notificationSentFutureResults = notifications.stream()
                .map(notification -> Pair.of(
                        notification,
                        mailService.sendSupportTicketSubmittedNotification(createNotificationDto(notification))
                ))
                .collect(Collectors.toList());

        var sentNotifications = notificationSentFutureResults.stream()
                .filter(it -> {
                    try {
                        return it.getSecond().get();
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Error on notification send result waiting", e);
                        return false;
                    }
                })
                .map(Pair::getFirst)
                .peek(notification -> {
                    notification.setSentDate(Instant.now());
                })
                .collect(Collectors.toList());

        ticketSubmittedNotificationDao.saveAll(sentNotifications);
    }

    private SupportTicketSubmittedNotificationDto createNotificationDto(SupportTicketSubmittedNotification notification) {
        var dto = new SupportTicketSubmittedNotificationDto();
        dto.setReceiverEmail(notification.getReceiverEmail());
        dto.setTicketNumber(notification.getTicket().getId());
        dto.setDate(notification.getTicket().getCreatedDate());
        dto.setAuthorOrganizationName(notification.getTicket().getAuthor().getOrganization().getName());
        dto.setAuthorName(notification.getTicket().getAuthor().getFullName());
        dto.setAuthorPhoneNumber(notification.getTicket().getAuthorPhoneNumber());
        dto.setHowCanWeHelpYouMessage(notification.getTicket().getType().getTitle());
        dto.setMessage(notification.getTicket().getMessage());
        dto.setAttachments(
                notification.getTicket().getAttachments().stream()
                        .map(this::createNotificationAttachmentDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private SupportTicketSubmittedNotificationDto.Attachment createNotificationAttachmentDto(SupportTicketAttachment attachment) {
        var dto = new SupportTicketSubmittedNotificationDto.Attachment();
        dto.setMediaType(attachment.getMimeType());
        dto.setFileName(attachment.getOriginalFileName());
        dto.setData(() -> supportTicketAttachmentFileStorage.loadAsInputStream(attachment.getFileName()));
        return dto;
    }
}
