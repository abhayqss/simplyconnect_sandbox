package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.schema.DeviceEvents;
import com.scnsoft.eldermark.schema.Events;
import com.scnsoft.eldermark.shared.carecoordination.AdtDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.events.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pzhurba on 25-Sep-15.
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface EventService {
    void processEvents(Events events);

    /**
     * Submit an event created manually to the backend system.
     * Persist the event.
     * Send the notifications to the patient/community care team members.
     */
    Event processManualEvent(EventDto eventDto);

    /**
     * Submit an event created automatically by the system to the backend system.
     * Persist the event.
     * Send the notifications to the patient/community care team members.
     */
    Event processAutomaticEvent(EventDto eventDto);

    Page<EventListItemDto> list(final Set<Long> employeeIds, EventFilterDto eventFilter, Pageable pageRequest);

    Map<Long, Long> countEventsForEachResidentIdForNonAdminEmployees(final Set<Long> employeeIds, Long databaseId, Set<Long> communityIds, Set<Long> employeeCommunityIds);

    void processAdtEvent(AdtDto adtDto, Long adtType);

    EventDto getEventDetails(final Long eventId);

    EventDto getEventDetailsWithoutNotes(final Long eventId);

    Date getEventsMinimumDate(List<Long> residentIds);

    Events createEvents(final Event event);

    void checkAccess(Long eventId);

    Integer getPageNumber(Long eventId, Set<Long> employeeIds);

    Integer getPageNumber(Long eventId, Set<Long> employeeIds, Long residentId);

    DeviceEventProcessingResultDto processDeviceEvents(DeviceEvents events);

    void createNotifyEvent(NotifyEventDto notifyEventDto);

    EventDto createEventDto(Event event, PatientDto patient, AdtMessage adtMessage);

    Event getById(Long eventId);
}
