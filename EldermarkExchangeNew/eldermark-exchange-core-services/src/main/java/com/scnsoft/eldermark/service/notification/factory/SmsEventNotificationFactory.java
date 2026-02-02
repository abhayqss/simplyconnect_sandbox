package com.scnsoft.eldermark.service.notification.factory;

import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.NotificationPreferences;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.UrlShortenerService;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SmsEventNotificationFactory extends BaseEventNotificationFactory {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private UrlService urlService;

    @Autowired
    private NoteDao noteDao;

    @Override
    protected String generateContent(Event event, NotificationPreferences np) {
        return createNotificationContentUtil(event, np, event.getEventType().getEventGroup().getName());
    }

    @Override
    protected String generateDestination(Employee employee) {
        return PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.MC)
                .or(() -> PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.WP))
                .orElse(StringUtils.EMPTY);
    }

    @Override
    protected String createLabNotificationContent(Event event, NotificationPreferences np, String eventTypeStr) {
        var patientName = buildPatientName(event);
        var labShortUrl = getLabShortUrl(event);

        return String.format(getLabEventContentTemplate(), patientName, labShortUrl);
    }

    @Override
    protected String createMAPNotificationContent(Event event, NotificationPreferences np, String eventTypeStr) {
        var patientName = buildPatientName(event);
        var mapShortUrl = getMAPShortUrl(event);

        return String.format(getMAPContentTemplate(), patientName, mapShortUrl);
    }

    @Override
    protected String createNoteNotificationContent(Event event, NotificationPreferences np, String eventTypeStr) {
        var action = getNoteAction(event.getEventType());
        var patientName = buildPatientName(event);
        var eventText = getNoteEventText(event, np, eventTypeStr);
        var communityName = getCommunityName(event);

        return String.format(getNoteContentTemplate(), action, patientName, communityName, eventText);
    }

    @Override
    protected String generateDestination(MobileUser mobileUser) {
        return mobileUser.getClientPhoneLegacy();
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.SMS;
    }

    @Override
    protected String getNoteContentTemplate() {
        return SMS_NOTE_NOTIFICATION_CONTENT_TEMPLATE;
    }

    @Override
    protected String getLabEventContentTemplate() {
        return EVENT_LAB_SMS_NOTIFICATION_CONTENT_TEMPLATE;
    }

    @Override
    protected String getMAPContentTemplate() {
        return SMS_MAP_NOTIFICATION_CONTENT_TEMPLATE;
    }

    @Override
    protected String getEventText(Event event, NotificationPreferences np, String eventTypeStr) {
        return urlShortenerService.getShortUrl(urlService.eventUrl(event));
    }

    @Override
    protected String getNoteEventText(Event event, NotificationPreferences np, String eventTypeStr) {
        var note = noteDao.findById(EventNotificationUtils.extractNotificationNoteId(event)).orElseThrow();
        return urlShortenerService.getShortUrl(urlService.noteUrl(note));
        //todo save data in event
    }

    private String getLabShortUrl(Event event) {
        return urlShortenerService.getShortUrl(urlService.labResearchOrderUrl(event.getLabResearchOrder()));
    }

    private String getMAPShortUrl(Event event) {
        return urlShortenerService.getShortUrl(urlService.clientDashboardUrl(event.getClient()));
    }
}
