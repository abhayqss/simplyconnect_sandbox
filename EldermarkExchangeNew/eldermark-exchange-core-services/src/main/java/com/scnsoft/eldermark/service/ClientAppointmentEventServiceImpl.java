package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAuthor;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClientAppointmentEventServiceImpl implements ClientAppointmentEventService {

    private static final ZoneId CST_TIMEZONE = ZoneId.of("America/Chicago");

    @Autowired
    private EventService eventService;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private ClientAppointmentNotificationService clientAppointmentNotificationService;

    @Override
    public void createAppointmentCreatedEvent(ClientAppointment appointment) {

        if (!appointment.getClient().getOrganization().getIsAppointmentsEnabled()) {
            return;
        }

        Event event = createEvent(
                appointment,
                appointment.getCreator(),
                EventNotificationUtils.APPOINTMENT_CREATED,
                "New appointment: " + getDateTimeAsString(appointment.getDateFrom()),
                appointment.getDirectionsInstructions()
        );

        eventService.save(event);

        clientAppointmentNotificationService.createStaffUpcomingAppointmentNotification(appointment);
        clientAppointmentNotificationService.createClientAppointmentReminderNotifications(appointment);
    }

    @Override
    public void createAppointmentUpdatedEvent(ClientAppointment prevAppointment, ClientAppointment newAppointment) {

        if (!newAppointment.getClient().getOrganization().getIsAppointmentsEnabled()) {
            return;
        }

        if (areCriticalFieldChanged(prevAppointment, newAppointment)) {

            var changes = new ArrayList<String>();

            changes.add("Appointment: " + getDateTimeAsString(newAppointment.getDateFrom()));

            if (!Objects.equals(prevAppointment.getLocation(), newAppointment.getLocation())) {
                changes.add("Location: " + newAppointment.getLocation());
            }

            if (!Objects.equals(prevAppointment.getDirectionsInstructions(), newAppointment.getDirectionsInstructions())) {
                changes.add("Appointment Directions & Instructions: " + newAppointment.getDirectionsInstructions());
            }

            if (!CareCoordinationUtils.isEqualNullableCollection(prevAppointment.getServiceProviders(), newAppointment.getServiceProviders())
                 || !Objects.equals(prevAppointment.getIsExternalProviderServiceProvider(), newAppointment.getIsExternalProviderServiceProvider())) {
                if (CollectionUtils.isEmpty(newAppointment.getServiceProviders()) && BooleanUtils.isFalse(newAppointment.getIsExternalProviderServiceProvider())) {
                    changes.add("Service provider: Not selected");
                } else {
                    if (CollectionUtils.isEmpty(newAppointment.getServiceProviders())) {
                        changes.add("Service provider: External Provider");
                    } else if (BooleanUtils.isFalse(newAppointment.getIsExternalProviderServiceProvider())) {
                        changes.add("Service provider: " + getServerProvidersAsString(newAppointment));
                    } else {
                        changes.add("Service provider:  External Provider, " + getServerProvidersAsString(newAppointment));
                    }
                }
            }

            if (!Objects.equals(prevAppointment.getDateFrom(), newAppointment.getDateFrom())
                    || !Objects.equals(prevAppointment.getDateTo(), newAppointment.getDateTo())) {
                changes.add("From: " + getDateTimeAsString(newAppointment.getDateFrom()));
                changes.add("To: " + getDateTimeAsString(newAppointment.getDateTo()));
            }

            Event event = createEvent(
                    newAppointment,
                    newAppointment.getCreator(),
                    EventNotificationUtils.APPOINTMENT_UPDATED,
                    String.join("\n", changes),
                    newAppointment.getDirectionsInstructions()
            );

            eventService.save(event);
            clientAppointmentNotificationService.createStaffUpcomingAppointmentNotification(newAppointment);
            clientAppointmentNotificationService.createClientAppointmentUpdatedNotification(newAppointment);
        }

        clientAppointmentNotificationService.createClientAppointmentReminderNotifications(newAppointment);
    }

    @Override
    public void createAppointmentCanceledEvent(ClientAppointment canceledAppointment) {

        if (!canceledAppointment.getClient().getOrganization().getIsAppointmentsEnabled()) {
            return;
        }

        Event event = createEvent(
                canceledAppointment,
                canceledAppointment.getCanceledBy(),
                EventNotificationUtils.APPOINTMENT_CANCELED,
                "Appointment cancellation: " + getDateTimeAsString(canceledAppointment.getLastModifiedDate()),
                null
        );

        eventService.save(event);

        clientAppointmentNotificationService.createClientAppointmentCanceledNotification(canceledAppointment);
    }

    @Override
    public void createAppointmentCompletedEvent(ClientAppointment updatedAppointment) {

        if (!updatedAppointment.getClient().getOrganization().getIsAppointmentsEnabled()) {
            return;
        }

        Event event = createEvent(
                updatedAppointment,
                updatedAppointment.getCreator(),
                EventNotificationUtils.APPOINTMENT_COMPLETED,
                "Appointment completed",
                null
        );

        eventService.save(event);

        clientAppointmentNotificationService.createClientAppointmentCompletedNotification(updatedAppointment);
    }

    private String getDateTimeAsString(Instant newAppointment) {
        return DateTimeUtils.formatDateTimeWithZone(newAppointment, CST_TIMEZONE);
    }

    private String getServerProvidersAsString(ClientAppointment clientAppointment) {
        return clientAppointment.getServiceProviders().stream()
                .map(e -> e.getFullName() + " - " + e.getCareTeamRole().getDisplayName())
                .collect(Collectors.joining(", "));
    }

    private Event createEvent(
            ClientAppointment appointment,
            Employee author,
            String eventTypeCode,
            String situation,
            String background
    ) {
        var event = new Event();
        event.setEventAuthor(createEventAuthor(author));
        event.setClient(appointment.getClient());
        event.setEventDateTime(appointment.getLastModifiedDate());
        event.setAppointmentChainId(
                appointment.getChainId() != null
                        ? appointment.getChainId()
                        : appointment.getId()
        );
        event.setBackground(background);
        event.setEventType(eventTypeService.findByCode(eventTypeCode));
        event.setLocation(appointment.getLocation() + ", " + appointment.getClient().getCommunity().getName());
        event.setSituation(situation);
        return event;
    }

    private EventAuthor createEventAuthor(Employee author) {
        var eventAuthor = new EventAuthor();
        eventAuthor.setFirstName(author.getFirstName());
        eventAuthor.setLastName(author.getLastName());
        eventAuthor.setOrganization(author.getOrganization().getName());
        eventAuthor.setRole(author.getCareTeamRole().getName());
        return eventAuthor;
    }

    private boolean areCriticalFieldChanged(ClientAppointment prevAppointment, ClientAppointment newAppointment) {
        return !(Objects.equals(prevAppointment.getLocation(), newAppointment.getLocation())
                && CareCoordinationUtils.isEqualNullableCollection(prevAppointment.getServiceProviders(), newAppointment.getServiceProviders())
                && Objects.equals(prevAppointment.getDirectionsInstructions(), newAppointment.getDirectionsInstructions())
                && Objects.equals(prevAppointment.getDateFrom(), newAppointment.getDateFrom())
                && Objects.equals(prevAppointment.getDateTo(), newAppointment.getDateTo())
                && Objects.equals(prevAppointment.getIsExternalProviderServiceProvider(), newAppointment.getIsExternalProviderServiceProvider()));
    }
}
