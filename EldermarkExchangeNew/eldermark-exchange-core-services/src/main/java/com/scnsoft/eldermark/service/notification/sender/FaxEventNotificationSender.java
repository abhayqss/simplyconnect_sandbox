package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;
import com.scnsoft.eldermark.dto.notification.event.EventDetailsNotificationDto;
import com.scnsoft.eldermark.dto.notification.event.EventFaxNotificationDto;
import com.scnsoft.eldermark.dto.notification.lab.LabEventFaxNotificationDto;
import com.scnsoft.eldermark.dto.notification.note.NoteDetailsNotificationDto;
import com.scnsoft.eldermark.dto.notification.note.NoteFaxNotificationDto;
import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.service.FaxService;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class FaxEventNotificationSender extends BaseEventNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(FaxEventNotificationSender.class);

    @Autowired
    private FaxService faxService;

    @Autowired
    private Converter<Event, EventDetailsNotificationDto> eventEventDetailsMailDtoConverter;

    @Autowired
    private Converter<Note, NoteDetailsNotificationDto> noteDetailsNotificationDtoConverter;

    @Autowired
    private Converter<NoteFaxNotificationDto, byte[]> noteFaxContentConverter;

    @Autowired
    private Converter<EventFaxNotificationDto, byte[]> eventFaxContentConverter;

    @Autowired
    private NoteDao noteDao;

    @Override
    protected boolean send(EventNotification eventNotification) {
        BaseFaxNotificationDto dto;
        byte[] content;
        if (EventNotificationUtils.isNoteNotification(eventNotification.getEvent().getEventType())) {
            var noteDto = convertNoteFaxDto(eventNotification);
            content = noteFaxContentConverter.convert(noteDto);
            dto = noteDto;
        } else if (EventNotificationUtils.isLabReviewedNotification(eventNotification.getEvent())) {
            //todo add labs notifications. Currently do nothing
            logger.info("Attempt to send Lab fax notification. Not implemented yet - notification won't be sent");
            return false;
        } else if (EventNotificationUtils.isMAPNotification(eventNotification.getEvent().getEventType())) {
            //todo add MAP notifications. Currently do nothing
            logger.info("Attempt to send MAP fax notification. Not implemented yet - notification won't be sent");
            return false;
        } else {
            var eventDto = convertEventFaxDto(eventNotification);
            content = eventFaxContentConverter.convert(eventDto);
            dto = eventDto;
        }

        return faxService.sendAndWait(dto, content);
    }

    private NoteFaxNotificationDto convertNoteFaxDto(EventNotification eventNotification) {
        var faxDto = new NoteFaxNotificationDto();
        fillBaseFaxDto(faxDto, eventNotification);

        var note = noteDao.findById(EventNotificationUtils.extractNotificationNoteId(eventNotification.getEvent())).orElseThrow();

        faxDto.setDetails(noteDetailsNotificationDtoConverter.convert(note));

        return faxDto;
    }

    private EventFaxNotificationDto convertEventFaxDto(EventNotification eventNotification) {
        var faxDto = new EventFaxNotificationDto();
        fillBaseFaxDto(faxDto, eventNotification);

        faxDto.setDetails(eventEventDetailsMailDtoConverter.convert(eventNotification.getEvent()));

        return faxDto;
    }

    private LabEventFaxNotificationDto convertLabEventFaxDto(EventNotification eventNotification) {
        var faxDto = new LabEventFaxNotificationDto();
        fillBaseFaxDto(faxDto, eventNotification);

        faxDto.setSubject("You have a new COVID-19 result(s) for " +
                eventNotification.getEvent().getClient().getFullName());

        return faxDto;

    }

    private void fillBaseFaxDto(BaseFaxNotificationDto faxDto, EventNotification eventNotification) {
        faxDto.setReceiverFullName(eventNotification.getPersonName());
        faxDto.setFrom("Simply Connect HIE");
        faxDto.setFaxNumber(eventNotification.getDestination());

        if (eventNotification.getEmployee() != null) {
            faxDto.setMobilePhone(PersonTelecomUtils.findValue(eventNotification.getEmployee().getPerson(), PersonTelecomCode.WP, null));
        }

        faxDto.setDate(Instant.now());
        faxDto.setSubject(generateSubject(eventNotification));
    }

    private String generateSubject(EventNotification eventNotification) {
        if (EventNotificationUtils.isNoteAdd(eventNotification.getEvent().getEventType())) {
            return "A note has been added to the Simply Connect platform";
        }

        if (EventNotificationUtils.isNoteEdit(eventNotification.getEvent().getEventType())) {
            return "A note has been updated in the Simply Connect platform";
        }

        return "A new event has been logged to the Simply Connect HIE";
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.FAX;
    }
}
