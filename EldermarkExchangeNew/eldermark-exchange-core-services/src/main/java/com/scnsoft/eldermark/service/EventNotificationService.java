package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventNotificationMessage;
import com.scnsoft.eldermark.entity.event.GroupedEventNotification;
import com.scnsoft.eldermark.entity.note.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventNotificationService {

    Page<GroupedEventNotification> find(Long eventId, Pageable pageable);

    List<EventNotificationMessage> find(Long eventId, Long employeeId, Long careTeamRoleId);

    void send(Event savedEvent);

    void send(Note savedEvent);

    Long count(Long eventId);

    void sendNewMAP(ClientDocument mapDocument);
}
