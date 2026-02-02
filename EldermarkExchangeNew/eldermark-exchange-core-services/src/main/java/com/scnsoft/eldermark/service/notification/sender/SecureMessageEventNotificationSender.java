package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.dto.notification.BaseNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.MAP.MAPNotificationSecureMailDto;
import com.scnsoft.eldermark.dto.notification.event.EventDetailsNotificationDto;
import com.scnsoft.eldermark.dto.notification.event.EventNotificationSecureMailDto;
import com.scnsoft.eldermark.dto.notification.lab.LabEventNotificationSecureMailDto;
import com.scnsoft.eldermark.dto.notification.note.NoteDetailsNotificationDto;
import com.scnsoft.eldermark.dto.notification.note.NoteNotificationSecureMailDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.service.DirectAccountDetailsFactory;
import com.scnsoft.eldermark.service.DirectAttachment;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.document.DocumentFileService;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class SecureMessageEventNotificationSender extends BaseEventNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(SecureMessageEventNotificationSender.class);

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private DirectAccountDetailsFactory directAccountDetailsFactory;

    @Autowired
    private UrlService urlService;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private Converter<Event, EventDetailsNotificationDto> eventDetailsMailDtoConverter;

    @Autowired
    private Converter<Note, NoteDetailsNotificationDto> noteDetailsNotificationDtoConverter;

    @Autowired
    private DocumentFileService documentFileService;

    @Autowired
    private DocumentDao documentDao;

    @Override
    protected boolean send(EventNotification eventNotification) {
        var employeeOrganization = eventNotification.getEmployee().getOrganization();
        var organizationDirectAccount = directAccountDetailsFactory.createOrganizationAccountDetails(employeeOrganization);

        var event = eventNotification.getEvent();
        if (EventNotificationUtils.isNoteNotification(event.getEventType())) {
            return exchangeMailService.sendSecureNoteNotificationAndWait(convertNoteNotification(eventNotification), organizationDirectAccount);
        }
        if (EventNotificationUtils.isLabReviewedNotification(event)) {
            return exchangeMailService.sendSecureLabEventNotificationAndWait(convertEventLabNotification(eventNotification), organizationDirectAccount);
        }
        if (EventNotificationUtils.isMAPNotification(event.getEventType())) {
            return exchangeMailService.sendSecureMapNotificationAndWait(convertMAPNotification(eventNotification), organizationDirectAccount);
        }
        return exchangeMailService.sendSecureEventNotificationAndWait(convertEventNotification(eventNotification), organizationDirectAccount);
    }

    private LabEventNotificationSecureMailDto convertEventLabNotification(EventNotification eventNotification) {
        var event = eventNotification.getEvent();

        var dto = new LabEventNotificationSecureMailDto();
        fillBaseNotificationDto(dto, eventNotification);
        dto.setLabOrderUrl(urlService.labResearchOrderUrl(event.getLabResearchOrder()));

        var documents = CollectionUtils.emptyIfNull(event.getLabResearchOrder().getDocuments()).stream()
                .map(this::buildAttachment)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        dto.setAttachments(documents);
        return dto;
    }

    private MAPNotificationSecureMailDto convertMAPNotification(EventNotification eventNotification) {
        var event = eventNotification.getEvent();

        var dto = new MAPNotificationSecureMailDto();
        fillBaseNotificationDto(dto, eventNotification);
        dto.setMapUrl(urlService.clientDashboardUrl(event.getClient()));

        var documents = buildAttachment(event.getMapDocumentId());
        dto.setMapPdf(documents);
        return dto;
    }

    private DirectAttachment buildAttachment(Long documentId) {
        return buildAttachment(documentDao.findById(documentId).orElseThrow());
    }

    private DirectAttachment buildAttachment(Document document) {
        //mb we should refactor to use ClientDocument or even some interface implemented by
        //both Document and ClientDocument?

        var attachment = new DirectAttachment();
        attachment.setFileName(document.getDocumentTitle());
        attachment.setContentType(document.getMimeType());

        try (var inputStream = documentFileService.loadDocument(document)) {
            attachment.setData(inputStream.readAllBytes());
        } catch (IOException e) {
            logger.warn("Failed to retrieve document [{}] bytes", document.getId(), e);
            return null;
        }

        return attachment;
    }

    private EventNotificationSecureMailDto convertEventNotification(EventNotification eventNotification) {
        var event = eventNotification.getEvent();

        var dto = new EventNotificationSecureMailDto();
        fillBaseNotificationDto(dto, eventNotification);

        dto.setEventGroup(event.getEventType().getEventGroup().getName());
        dto.setEventType(event.getEventType().getDescription());
        dto.setCommunityName(event.getClient().getCommunity().getName());
        dto.setResponsibility(eventNotification.getResponsibility().getDescription());
        dto.setEventUrl(urlService.eventUrl(event));
        dto.setDetails(eventDetailsMailDtoConverter.convert(event));

        return dto;
    }

    private NoteNotificationSecureMailDto convertNoteNotification(EventNotification eventNotification) {
        var note = noteDao.findById(EventNotificationUtils.extractNotificationNoteId(eventNotification.getEvent())).orElseThrow();

        var dto = new NoteNotificationSecureMailDto();
        fillBaseNotificationDto(dto, eventNotification);

        dto.setClientName(note.getNoteClients().stream()
                .map(Client::getFullName)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", ")));

        dto.setAction(EventNotificationUtils.getNoteNotificationAction(eventNotification.getEvent().getEventType()));
        dto.setNoteUrl(urlService.noteUrl(note));

        dto.setDetails(noteDetailsNotificationDtoConverter.convert(note));

        return dto;
    }

    private void fillBaseNotificationDto(BaseNotificationMailDto dto, EventNotification eventNotification) {
        dto.setClientName(eventNotification.getEvent().getClient().getFullName());
        dto.setReceiverFullName(eventNotification.getPersonName());
        dto.setReceiverEmail(eventNotification.getDestination());
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.SECURITY_MESSAGE;
    }
}
