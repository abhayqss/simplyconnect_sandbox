package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.SupportTicketDao;
import com.scnsoft.eldermark.dao.SupportTicketSubmittedNotificationDao;
import com.scnsoft.eldermark.dto.support.SubmitSupportTicketDto;
import com.scnsoft.eldermark.entity.SupportTicket;
import com.scnsoft.eldermark.entity.SupportTicketAttachment;
import com.scnsoft.eldermark.entity.SupportTicketSubmittedNotification;
import com.scnsoft.eldermark.service.storage.SupportTicketAttachmentFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    @Autowired
    private SupportTicketDao ticketDao;

    @Autowired
    private SupportTicketAttachmentFileStorage ticketAttachmentFileStorage;

    @Autowired
    private SupportConfigurationService supportConfigurationService;

    @Autowired
    private SupportTicketNotificationService ticketNotificationService;

    @Autowired
    private SupportTicketSubmittedNotificationDao ticketSubmittedNotificationDao;

    @Override
    @Transactional
    public SupportTicket submit(SubmitSupportTicketDto dto) {

        var ticket = createTicket(dto);

        ticketNotificationService.sendNotifications(createTicketSubmittedNotifications(ticket));

        return ticket;
    }

    private List<SupportTicketSubmittedNotification> createTicketSubmittedNotifications(SupportTicket ticket) {
        var notificationCreatedDate = Instant.now();
        var notifications = supportConfigurationService.getSupportTicketReceiverEmails().stream()
                .map(receiverEmail -> {
                    var notification = new SupportTicketSubmittedNotification();
                    notification.setTicket(ticket);
                    notification.setCreatedDate(notificationCreatedDate);
                    notification.setReceiverEmail(receiverEmail);
                    return notification;
                })
                .collect(Collectors.toList());
        return ticketSubmittedNotificationDao.saveAll(notifications);
    }

    private SupportTicket createTicket(SubmitSupportTicketDto dto) {
        var ticket = new SupportTicket();
        ticket.setMessage(dto.getMessage());
        ticket.setAuthor(dto.getAuthor());
        ticket.setAuthorPhoneNumber(dto.getAuthorPhoneNumber());
        ticket.setCreatedDate(dto.getCreationDate());
        ticket.setType(dto.getType());
        ticket.setAttachments(
                dto.getAttachmentFiles().stream()
                        .map(file -> createTicketAttachment(ticket, file))
                        .collect(Collectors.toList())
        );
        return ticketDao.save(ticket);
    }

    private SupportTicketAttachment createTicketAttachment(SupportTicket ticket, MultipartFile file) {
        var attachment = new SupportTicketAttachment();
        attachment.setTicket(ticket);
        attachment.setOriginalFileName(file.getOriginalFilename());
        attachment.setMimeType(file.getContentType());
        var fileName = ticketAttachmentFileStorage.save(file);
        attachment.setFileName(fileName);
        return attachment;
    }

    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return ticketDao.findById(id, projection).orElseThrow();
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return ticketDao.findByIdIn(ids, projection);
    }
}
