package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentNotificationMethod;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentReminder;
import com.scnsoft.eldermark.entity.event.AppointmentNotificationType;
import com.scnsoft.eldermark.entity.event.ClientAppointmentNotification;
import com.scnsoft.eldermark.entity.event.DeferredAppointmentNotification;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentReminder.*;
import static com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentReminder.WEEK_1_BEFORE;

@Component
public class ClientAppointmentNotificationBuilder {

    private static final Set<ClientAppointmentNotificationMethod> SUPPORTED_NOTIFICATION_METHODS = Set.of(
            ClientAppointmentNotificationMethod.EMAIL,
            ClientAppointmentNotificationMethod.SMS
    );

    private final Map<ClientAppointmentReminder, Function<Instant, Instant>> reminderTimeMap = Map.of(
            NEVER, time -> null,
            AT_EVENT, Function.identity(),
            MIN_15_BEFORE, time -> time.minus(15, ChronoUnit.MINUTES),
            MIN_30_BEFORE, time -> time.minus(30, ChronoUnit.MINUTES),
            HOUR_1_BEFORE, time -> time.minus(1, ChronoUnit.HOURS),
            HOUR_2_BEFORE, time -> time.minus(2, ChronoUnit.HOURS),
            DAY_1_BEFORE, time -> time.minus(1, ChronoUnit.DAYS),
            WEEK_1_BEFORE, time -> time.minus(1, ChronoUnit.WEEKS)
    );

    public DeferredAppointmentNotification createDeferredStaffUpcomingAppointmentNotification(ClientAppointment appointment, Instant dispatchTime) {
        var notification = new DeferredAppointmentNotification();
        notification.setType(AppointmentNotificationType.STAFF_UPCOMING_APPOINTMENT);
        notification.setDispatchDatetime(dispatchTime);
        notification.setAppointment(appointment);
        return notification;
    }

    public List<ClientAppointmentNotification> createClientNotifications(
            ClientAppointment appointment,
            AppointmentNotificationType notificationType
    ) {
        if (appointment.getReminders().contains(NEVER)) return List.of();
        return appointment.getNotificationMethods().stream()
                .filter(SUPPORTED_NOTIFICATION_METHODS::contains)
                .map(notificationMethod -> {
                    var notification = new ClientAppointmentNotification();
                    notification.setAppointment(appointment);
                    notification.setNotificationMethod(notificationMethod);
                    notification.setNotificationType(notificationType);
                    notification.setCreatedDatetime(Instant.now());
                    return notification;
                })
                .collect(Collectors.toList());
    }

    public List<DeferredAppointmentNotification> createDeferredReminderNotifications(ClientAppointment appointment) {
        var nowTime = Instant.now();
        return appointment.getReminders().stream()
                .flatMap(reminder -> {
                    if (!reminderTimeMap.containsKey(reminder)) {
                        throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED, reminder.name() + " is not implemented");
                    }

                    var eventTime = appointment.getDateFrom();

                    var dispatchTime = reminderTimeMap.get(reminder).apply(eventTime);

                    if (dispatchTime != null && dispatchTime.isAfter(nowTime)) {
                        var notification = new DeferredAppointmentNotification();
                        notification.setAppointment(appointment);
                        notification.setType(AppointmentNotificationType.CLIENT_REMINDER);
                        notification.setDispatchDatetime(dispatchTime);
                        return Stream.of(notification);
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());
    }
}
