package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SmsEventNotificationSender extends BaseEventNotificationSender {

    @Autowired
    private SmsService smsService;

    @Override
    protected boolean send(EventNotification eventNotification) {
        return smsService.sendSmsNotificationAndWait(eventNotification.getDestination(), eventNotification.getContent());
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.SMS;
    }
}
