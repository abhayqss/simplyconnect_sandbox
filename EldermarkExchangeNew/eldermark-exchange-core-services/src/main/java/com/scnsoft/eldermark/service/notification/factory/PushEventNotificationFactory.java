package com.scnsoft.eldermark.service.notification.factory;

import com.scnsoft.eldermark.dao.phr.MobileUserDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.NotificationPreferences;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PushEventNotificationFactory extends BaseEventNotificationFactory {

    @Autowired
    private MobileUserDao mobileUserDao;

    @Override
    protected String generateContent(Event event, NotificationPreferences np) {
        return createNotificationContentUtil(event, np, event.getEventType().getDescription());
    }

    @Override
    protected String generateDestination(Employee employee) {
        return generateDestination(mobileUserDao.findByEmployee(employee));
    }

    @Override
    protected String generateDestination(MobileUser mobileUser) {
        return String.valueOf(mobileUser.getId());
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.PUSH_NOTIFICATION;
    }
}
