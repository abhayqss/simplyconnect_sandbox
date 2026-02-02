package com.scnsoft.eldermark.service.notification.factory;


import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.NotificationPreferences;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.service.DirectAccountDetailsFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecureMessageEventNotificationFactory extends BaseEventNotificationFactory {

    @Autowired
    private DirectAccountDetailsFactory directAccountDetailsFactory;

    @Override
    protected String generateContent(Event event, NotificationPreferences np) {
        //content is not used during secure email notification sending
        return createNotificationContentUtil(event, np, event.getEventType().getDescription());
    }

    @Override
    protected String generateDestination(Employee employee) {
        return directAccountDetailsFactory.createMailAccountDetails(employee).getSecureEmail();
    }

    @Override
    protected String generateDestination(MobileUser mobileUser) {
        if (mobileUser.getEmployee() != null) {
            return generateDestination(mobileUser.getEmployee());
        }
        return StringUtils.EMPTY;
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.SECURITY_MESSAGE;
    }

    @Override
    protected String fetchClientName(Client client) {
        return client.getFullName();
    }
}
