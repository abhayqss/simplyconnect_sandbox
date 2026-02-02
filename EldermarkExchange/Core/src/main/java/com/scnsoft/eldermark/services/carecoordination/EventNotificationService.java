package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventNotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mradzivonenka
 * @author pzhurba
Created by pzhurba on 28-Sep-15.
 */
@Transactional
public interface EventNotificationService {

    void createNotifications(Event eventEntity, EventDto eventDetails);

    Page<EventNotificationDto> getEventNotifications(Long eventId, Pageable pageRequest, Boolean isSend);

    void createNotificationsForAdmin(EventNotification employeeNotification);
}
