package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ClientAppointmentNotificationDao;
import com.scnsoft.eldermark.dto.notification.AppointmentEmailNotificationDto;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentNotificationMethod;
import com.scnsoft.eldermark.entity.event.AppointmentNotificationType;
import com.scnsoft.eldermark.entity.event.ClientAppointmentNotification;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class AppointmentNotificationSenderImpl implements AppointmentNotificationSender {

    private final static Logger logger = LoggerFactory.getLogger(AppointmentNotificationSenderImpl.class);

    @Autowired
    private SmsService smsService;

    @Autowired
    private ExchangeMailService mailService;

    @Autowired
    private ClientAppointmentNotificationDao clientAppointmentNotificationDao;

    @Autowired
    private EventService eventService;

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private UrlService urlService;

    private final Map<AppointmentNotificationType, Function<ClientAppointment, String>> SMS_GENERATOR_MAP = Map.of(
            AppointmentNotificationType.CLIENT_UPCOMING_APPOINTMENT, this::generateUpcomingAppointmentSmsMessage,
            AppointmentNotificationType.CLIENT_REMINDER, this::generateUpcomingAppointmentSmsMessage,
            AppointmentNotificationType.CLIENT_UPDATED_EVENT, this::generateAppointmentUpdatedSmsMessage,
            AppointmentNotificationType.CLIENT_CANCELED_EVENT, this::generateAppointmentCanceledSmsMessage,
            AppointmentNotificationType.CLIENT_COMPLETED_EVENT, this::generateAppointmentCompletedSmsMessage
    );

    private final Map<AppointmentNotificationType, Function<ClientAppointment, Boolean>> EMAIL_SENDER_MAP = Map.of(
            AppointmentNotificationType.CLIENT_UPCOMING_APPOINTMENT, this::sendUpcomingAppointmentEmailNotification,
            AppointmentNotificationType.CLIENT_REMINDER, this::sendUpcomingAppointmentEmailNotification,
            AppointmentNotificationType.CLIENT_UPDATED_EVENT, this::sendAppointmentUpdatedEmailNotification,
            AppointmentNotificationType.CLIENT_CANCELED_EVENT, this::sendAppointmentCanceledNotification,
            AppointmentNotificationType.CLIENT_COMPLETED_EVENT, this::sendAppointmentCompletedNotification
    );

    private final Map<ClientAppointmentNotificationMethod, Function<ClientAppointmentNotification, Boolean>> NOTIFICATION_SENDER_MAP = Map.of(
            ClientAppointmentNotificationMethod.SMS, this::sendSmsClientNotification,
            ClientAppointmentNotificationMethod.EMAIL, this::sendEmailClientNotification
    );

    @Override
    public void sendClientNotifications(List<ClientAppointmentNotification> notifications) {
        notifications.forEach(this::sendClientNotification);
    }

    @Override
    public void sendClientNotification(ClientAppointmentNotification notification) {

        var notificationSender = NOTIFICATION_SENDER_MAP.get(notification.getNotificationMethod());

        if (notificationSender == null) {
            logger.warn("Unsupported appointment client notification method: {}", notification.getNotificationMethod().name());
            return;
        }

        if (notificationSender.apply(notification)) {
            notification.setSentDatetime(Instant.now());
            clientAppointmentNotificationDao.save(notification);
        }
    }

    @Override
    public void sendStaffNotification(ClientAppointment clientAppointment, AppointmentNotificationType type) {
        if (type == AppointmentNotificationType.STAFF_UPCOMING_APPOINTMENT) {
            eventService.findLastByAppointmentChainIdAndEventTypeCodes(
                            clientAppointment.getChainId() == null
                                    ? clientAppointment.getId()
                                    : clientAppointment.getChainId(),
                            List.of(
                                    EventNotificationUtils.APPOINTMENT_UPDATED,
                                    EventNotificationUtils.APPOINTMENT_CREATED
                            )
                    )
                    .ifPresent(eventService::sendEventNotification);
        }
    }

    private boolean sendEmailClientNotification(ClientAppointmentNotification notification) {

        var emailSender = EMAIL_SENDER_MAP.get(notification.getNotificationType());

        if (emailSender == null) {
            logger.warn("Unsupported appointment client notification type: {}", notification.getNotificationType().name());
            return false;
        }

        return emailSender.apply(notification.getAppointment());
    }

    private boolean sendUpcomingAppointmentEmailNotification(ClientAppointment appointment) {
        return mailService.sendUpcomingAppointmentNotification(createEmailNotificationDto(appointment));
    }

    private boolean sendAppointmentUpdatedEmailNotification(ClientAppointment appointment) {
        return mailService.sendAppointmentUpdatedNotification(createEmailNotificationDto(appointment));
    }

    private boolean sendAppointmentCanceledNotification(ClientAppointment appointment) {
        return mailService.sendAppointmentCanceledNotification(createEmailNotificationDto(appointment));
    }

    private boolean sendAppointmentCompletedNotification(ClientAppointment appointment) {
        return mailService.sendAppointmentCompletedNotification(createEmailNotificationDto(appointment));
    }

    private AppointmentEmailNotificationDto createEmailNotificationDto(ClientAppointment appointment) {
        var dto = new AppointmentEmailNotificationDto();
        dto.setReceiverEmail(appointment.getEmail());
        dto.setReceiverFullName(appointment.getClient().getFullName());
        dto.setCommunityName(appointment.getClient().getCommunity().getName());
        dto.setLocation(appointment.getLocation());
        dto.setAppointmentDateTime(DateTimeUtils.formatDateTimeWithZone(appointment.getDateFrom(), DateTimeUtils.CST_TIMEZONE));
        dto.setAppointmentDate(DateTimeUtils.formatDate(appointment.getDateFrom(), DateTimeUtils.CST_TIMEZONE));
        dto.setAppointmentTime(DateTimeUtils.formatTimeWithZone(appointment.getDateFrom(), DateTimeUtils.CST_TIMEZONE));
        dto.setAppointmentUrl(urlService.appointmentUrl(appointment));
        return dto;
    }

    private boolean sendSmsClientNotification(ClientAppointmentNotification notification) {
        var phone = notification.getAppointment().getPhone();
        var smsGenerator = SMS_GENERATOR_MAP.get(notification.getNotificationType());

        if (smsGenerator == null) {
            logger.warn("Unsupported appointment client notification type: {}", notification.getNotificationMethod().name());
            return false;
        }

        var sms = smsGenerator.apply(notification.getAppointment());

        return smsService.sendSmsNotificationAndWait(phone, sms);
    }

    private String generateUpcomingAppointmentSmsMessage(ClientAppointment appointment) {
        if (clientHasAccount(appointment)) {
            return String.format(
                    "You have an appointment scheduled.\n%s %s;%s: %s",
                    appointment.getClient().getCommunity().getName(),
                    appointment.getLocation(),
                    getFormattedAppointmentTime(appointment),
                    urlShortenerService.getShortUrl(urlService.appointmentUrl(appointment))
            );
        } else {
            return String.format(
                    "You have an appointment scheduled.\n%s %s;%s.",
                    appointment.getClient().getCommunity().getName(),
                    appointment.getLocation(),
                    getFormattedAppointmentTime(appointment)
            );
        }
    }

    private String generateAppointmentUpdatedSmsMessage(ClientAppointment appointment) {
        if (clientHasAccount(appointment)) {
            return String.format(
                    "Your appointment on %s at %s %s has been updated: %s",
                    getFormattedAppointmentTime(appointment),
                    appointment.getClient().getCommunity().getName(),
                    appointment.getLocation(),
                    urlShortenerService.getShortUrl(urlService.appointmentUrl(appointment))
            );
        } else {
            return String.format(
                    "Your appointment on %s at %s %s has been updated.",
                    getFormattedAppointmentTime(appointment),
                    appointment.getClient().getCommunity().getName(),
                    appointment.getLocation()
            );
        }
    }

    private String generateAppointmentCanceledSmsMessage(ClientAppointment appointment) {
        if (clientHasAccount(appointment)) {
            return String.format(
                    "Your appointment on %s at %s %s has been cancelled: %s",
                    getFormattedAppointmentTime(appointment),
                    appointment.getClient().getCommunity().getName(),
                    appointment.getLocation(),
                    urlShortenerService.getShortUrl(urlService.appointmentUrl(appointment))
            );
        } else {
            return String.format(
                    "Your appointment on %s at %s %s has been cancelled.",
                    getFormattedAppointmentTime(appointment),
                    appointment.getClient().getCommunity().getName(),
                    appointment.getLocation()
            );
        }
    }

    private String generateAppointmentCompletedSmsMessage(ClientAppointment appointment) {
        if (clientHasAccount(appointment)) {
            return String.format(
                    "Your appointment on %s at %s %s has been completed: %s",
                    getFormattedAppointmentTime(appointment),
                    appointment.getClient().getCommunity().getName(),
                    appointment.getLocation(),
                    urlShortenerService.getShortUrl(urlService.appointmentUrl(appointment))
            );
        } else {
            return String.format(
                    "Your appointment on %s at %s %s has been completed.",
                    getFormattedAppointmentTime(appointment),
                    appointment.getClient().getCommunity().getName(),
                    appointment.getLocation()
            );
        }
    }

    private String getFormattedAppointmentTime(ClientAppointment appointment) {
        return DateTimeUtils.formatDateTimeWithZone(appointment.getDateFrom(), DateTimeUtils.CST_TIMEZONE);
    }

    private boolean clientHasAccount(ClientAppointment appointment) {
        return appointment.getClient().getAssociatedEmployee() != null;
    }
}
