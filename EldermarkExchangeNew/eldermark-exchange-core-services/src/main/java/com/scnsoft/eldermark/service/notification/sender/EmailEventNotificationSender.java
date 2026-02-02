package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.dto.notification.BaseNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.MAP.MAPNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.event.EventNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.lab.LabEventNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.note.NoteNotificationMailDto;
import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.util.ClientUtils;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class EmailEventNotificationSender extends BaseEventNotificationSender {

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private UrlService urlService;

    @Autowired
    private NoteDao noteDao;

    @Override
    protected boolean send(EventNotification eventNotification) {
        var event = eventNotification.getEvent();
        if (EventNotificationUtils.isNoteNotification(event.getEventType())) {
            return exchangeMailService.sendNoteNotificationAndWait(convertNoteNotification(eventNotification));
        }
        if (EventNotificationUtils.isLabReviewedNotification(event)) {
            return exchangeMailService.sendLabEventNotificationAndWait(convertLabNotification(eventNotification));
        }
        if (EventNotificationUtils.isMAPNotification(event.getEventType())) {
            return exchangeMailService.sendMAPEventNotificationAndWait(convertMAPNotification(eventNotification));
        }
        return exchangeMailService.sendEventNotificationAndWait(convertEventNotification(eventNotification));

    }

    private EventNotificationMailDto convertEventNotification(EventNotification eventNotification) {
        var event = eventNotification.getEvent();

        var dto = new EventNotificationMailDto();
        fillBaseNotificationDto(dto, eventNotification);

        dto.setEventGroup(event.getEventType().getEventGroup().getName());
        dto.setEventType(event.getEventType().getDescription());
        dto.setCommunityName(event.getClient().getCommunity().getName());
        dto.setResponsibility(eventNotification.getResponsibility().getDescription());
        if (EventNotificationUtils.isLabReviewedNotification(event)) {
            dto.setEventUrl(urlService.labResearchOrderUrl(event.getLabResearchOrder()));
        } else {
            dto.setEventUrl(urlService.eventUrl(event));
        }
        return dto;
    }

    private NoteNotificationMailDto convertNoteNotification(EventNotification eventNotification) {
        var note = noteDao.findById(EventNotificationUtils.extractNotificationNoteId(eventNotification.getEvent())).orElseThrow();

        var dto = new NoteNotificationMailDto();
        fillBaseNotificationDto(dto, eventNotification);

        dto.setClientName(note.getNoteClients().stream()
                .map(client -> ClientUtils.getInitials(client, " "))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", ")));

        dto.setAction(EventNotificationUtils.getNoteNotificationAction(eventNotification.getEvent().getEventType()));
        dto.setNoteUrl(urlService.noteUrl(note));
        dto.setCommunityName(note.getNoteClients().stream().findAny().map(client -> client.getCommunity().getName()).orElseThrow());

        return dto;
    }

    private LabEventNotificationMailDto convertLabNotification(EventNotification eventNotification) {
        var dto = new LabEventNotificationMailDto();

        fillBaseNotificationDto(dto, eventNotification);

        dto.setLabOrderUrl(urlService.labResearchOrderUrl(eventNotification.getEvent().getLabResearchOrder()));
        return dto;
    }

    private MAPNotificationMailDto convertMAPNotification(EventNotification eventNotification) {
        var dto = new MAPNotificationMailDto();

        fillBaseNotificationDto(dto, eventNotification);

        dto.setMapUrl(urlService.clientDashboardUrl(eventNotification.getEvent().getClient()));
        return dto;
    }

    private void fillBaseNotificationDto(BaseNotificationMailDto dto, EventNotification eventNotification) {
        dto.setClientName(ClientUtils.getInitials(eventNotification.getEvent().getClient(), " "));
        dto.setReceiverFullName(eventNotification.getPersonName());
        dto.setReceiverEmail(eventNotification.getDestination());
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.EMAIL;
    }
}
