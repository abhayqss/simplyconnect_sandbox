package com.scnsoft.eldermark.service.notification.factory;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.NotificationPreferences;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlueStoneEventNotificationFactory extends BaseEventNotificationFactory {

    @Override
    protected String generateContent(Event event, NotificationPreferences np) {
        return event.getEventContent(); //todo generate XML content on event save
    }

    @Override
    protected String generateDestination(Employee employee) {
        return StringUtils.EMPTY;
    }

    @Override
    protected String generateDestination(MobileUser mobileUser) {
        throw new BusinessException(BusinessExceptionType.UNSUPPORTED_NOTIFICATION_TYPE);
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.BLUE_STONE;
    }
}
