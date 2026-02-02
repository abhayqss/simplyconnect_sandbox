package com.scnsoft.eldermark.service.notification.factory;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.NotificationPreferences;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailEventNotificationFactory extends BaseEventNotificationFactory {

    @Override
    protected String generateContent(Event event, NotificationPreferences np) {
        //content is not used during email notification sending
        return createNotificationContentUtil(event, np, event.getEventType().getDescription());
    }

    @Override
    protected String generateDestination(Employee employee) {
        return PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.EMAIL, StringUtils.EMPTY);
    }

    @Override
    protected String generateDestination(MobileUser mobileUser) {
        return mobileUser.getClientEmailLegacy();
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.EMAIL;
    }
}
